package com.backbase.movies.service;

import com.backbase.movies.client.OMDbClient;
import com.backbase.movies.exception.MovieHttpException;
import com.backbase.movies.model.Movie;
import com.backbase.movies.model.MovieRatings;
import com.backbase.movies.model.MovieResponse;
import com.backbase.movies.model.RatingResponse;
import com.backbase.movies.model.generated.MovieResult;
import com.backbase.movies.repository.MovieRatingRepository;
import com.backbase.movies.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Holds the Business logic to fetch / update the movie data
 *
 * @author Revathi
 */
@Service
@Slf4j
public class MovieService {
    /**
     * Field omdbClient
     */
    private final OMDbClient omDbClient;
    /**
     * Field MovieRepository
     */
    private final MovieRepository movieRepository;
    /**
     * Field MovieRatingRespository
     */
    private final MovieRatingRepository movieRatingRepository;

    /**
     * Consturction Instantiation with omDbClient, movieRepository, movieRatingRepository
     *
     * @param omDbClient  omDbClient
     * @param movieRepository movieRepository
     * @param movieRatingRepository movieRatingRepository
     */
    public MovieService(OMDbClient omDbClient, MovieRepository movieRepository, MovieRatingRepository movieRatingRepository) {
        this.omDbClient = omDbClient;
        this.movieRepository = movieRepository;
        this.movieRatingRepository = movieRatingRepository;
    }

    /**
     * Fetch the movie data from omdb database after validating the user given apiKey and the movieName
     * Insert/Update the moving rating and boxoffice Value into the database.
     *
     * @param apiKey    apiKeyValue
     * @param movieName movieName
     * @return MovieResponse object
     * @throws MovieHttpException with Http status and Message
     */
    public MovieResponse getMovieData(String apiKey, String movieName) throws MovieHttpException {

        MovieResult movieResult = validateKeyAndMovie(apiKey, movieName);
        log.info("Validated Movie {} with provided apiKey", movieResult.getTitle());

        //Retrieve whether movie won Oscar
        Movie oscarData = retrieveMovieOscarData(movieResult);

        //Insert/Update Imdb Rating in Rating table
        Double rating = Double.valueOf(movieResult.getImdbRating());
        MovieRatings ratings = addMovieRating(movieResult.getTitle(), rating, formatBoxOfficeValue(movieResult.getBoxOffice()), true);

        //Prepare Final Response
        return prepareMovieResponse(oscarData.getWinner(), movieResult, ratings.getRating());
    }

    /**
     * Update Movie Rating in database after validating apiKey and movieName
     *
     * @param apiKey    apiKeyValue
     * @param movieName movieName
     * @param rating    rating value
     * @return RatingResponse Object
     * @throws MovieHttpException with Http status and Message
     */
    public RatingResponse updateMovieRating(String apiKey, String movieName, Double rating) throws MovieHttpException {
        MovieResult movieResult = validateKeyAndMovie(apiKey, movieName);
        log.info("Validated Movie Rating {} with provided apiKey", movieResult.getTitle());

        MovieRatings movieRatings
                = addMovieRating(movieResult.getTitle(), rating, formatBoxOfficeValue(movieResult.getBoxOffice()), false);
        return prepareRatingResponse(movieRatings);
    }

    /**
     * Fetches the Top10 Movies based on their box office value
     *
     * @return List of RatingResponse objects
     */
    public List<RatingResponse> getBoxOfficeTop10Movies() {
        List<MovieRatings> movieRatings = movieRatingRepository.findTop10ByOrderByBoxOfficeValueDesc();
        return movieRatings.stream().map(this::prepareRatingResponse).collect(Collectors.toList());
    }

    private Movie retrieveMovieOscarData(MovieResult movieResult) {
        return movieRepository.findByMovieName(movieResult.getTitle());
    }

    private Integer formatBoxOfficeValue(String boxOffice) {
        if (boxOffice.equalsIgnoreCase("N/A")) {
            return 0;
        }
        return Integer.valueOf(boxOffice.replaceAll("[$,]", ""));
    }

    private MovieRatings addMovieRating(String title, Double rating, Integer boxOfficeValue, boolean imdbRating) {
        //fetch the movie
        Optional<MovieRatings> mvRating = Optional.ofNullable(movieRatingRepository.findByMovieName(title));
        MovieRatings movieRating;

        if (mvRating.isPresent()) {

            //Update rating by calculating with existing and current ratings only when User updates the rating.
            rating = imdbRating ? rating : calculateRating(mvRating.get().getRating(), rating, mvRating.get().getNoOfRatings());
            int noOfRatings = imdbRating ? mvRating.get().getNoOfRatings() : mvRating.get().getNoOfRatings() +1;

            movieRating = MovieRatings.builder()
                    .ratingId(mvRating.get().getRatingId())
                    .movieName(mvRating.get().getMovieName())
                    .rating(rating)
                    .boxOfficeValue(boxOfficeValue)
                    .noOfRatings(noOfRatings).build();
        } else {
            //update rating as current rating as its new
            movieRating = MovieRatings.builder()
                    .movieName(title)
                    .rating(rating)
                    .boxOfficeValue(boxOfficeValue)
                    .noOfRatings(1).build();
        }
        return movieRatingRepository.save(movieRating);
    }

    private Double calculateRating(Double existingRating, Double currentRating, int noOfRatings) {
        //Round to one decimal place
        double scale = Math.pow(10, 1);
        return Math.round(((existingRating * noOfRatings) + currentRating) / (noOfRatings + 1) * scale) / scale;
    }

    private MovieResult validateKeyAndMovie(String apiKey, String movieName) throws MovieHttpException {
        return omDbClient.getMovieData(apiKey, movieName);
    }

    private MovieResponse prepareMovieResponse(String winner, MovieResult movieResult, double rating) {
        return MovieResponse.builder()
                .movieName(movieResult.getTitle())
                .releaseDate(movieResult.getReleased())
                .rating(rating)
                .boxOfficeValue(movieResult.getBoxOffice())
                .isWonOscar(winner)
                .build();
    }

    private RatingResponse prepareRatingResponse(MovieRatings movieRatings) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        return RatingResponse.builder()
                .ratingId(movieRatings.getRatingId())
                .movieName(movieRatings.getMovieName())
                .rating(movieRatings.getRating())
                .boxOfficeValue(nf.format(movieRatings.getBoxOfficeValue()))
                .build();
    }
}