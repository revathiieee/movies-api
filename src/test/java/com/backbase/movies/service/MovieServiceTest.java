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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieRatingRepository movieRatingRepository;

    @Mock
    private OMDbClient omDbClient;

    private String apiKey;
    private String movieName;

    @BeforeEach
    void setup() {
        apiKey = "123bcd";
        movieName = "Titanic";
    }

    @Test
    void isMovieWonOscar() throws MovieHttpException {
        //Prepare
        when(omDbClient.getMovieData("123bcd", "Titanic")).thenReturn(prepareMovieResult("9.3"));
        when(movieRepository.findByMovieName("Titanic")).thenReturn(prepareMovie());
        when(movieRatingRepository.findByMovieName("Titanic")).thenReturn(prepareMovieRatings(10, 9.3));
        when(movieRatingRepository.save(prepareMovieRatings(11, 9.3))).thenReturn(prepareMovieRatings(11, 9.3));

        //Test
        MovieResponse response = movieService.getMovieData("123bcd", "Titanic");

        //Verify
        assertNotNull(response);
        assertEquals("Titanic", response.getMovieName());
        assertEquals("YES", response.getIsWonOscar());
    }

    @Test
    void isMovieWonOscar_firstRating() throws MovieHttpException {
        //Prepare
        when(omDbClient.getMovieData("123bcd", "Titanic")).thenReturn(prepareMovieResult("9.3"));
        when(movieRepository.findByMovieName("Titanic")).thenReturn(prepareMovie());
        when(movieRatingRepository.findByMovieName("Titanic")).thenReturn(null);
        MovieRatings updatedRating = MovieRatings.builder().ratingId(null).movieName("Titanic").rating(9.3).noOfRatings(1).boxOfficeValue(123344456).build();
        when(movieRatingRepository.save(updatedRating)).thenReturn(prepareMovieRatings(1, 9.3));

        //Test
        MovieResponse response = movieService.getMovieData("123bcd", "Titanic");

        //Verify
        assertNotNull(response);
        verify(movieRatingRepository).save(updatedRating);
    }

    @Test
    void movieNotFound() {
        //Prepare
        when(omDbClient.getMovieData("123bcd", "invalidMovie"))
                .thenThrow(MovieHttpException.notFound("MovieName : invalidMovie does not exist."));

        //Test
        Exception exception = assertThrows(MovieHttpException.class, () -> {
            movieService.getMovieData("123bcd", "invalidMovie");
        });

        //Verify
        String expectedMessage = "MovieName : invalidMovie does not exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void invalidApiKey() {
        //Prepare
        when(omDbClient.getMovieData("212Invalid", "Titanic"))
                .thenThrow(MovieHttpException.unAuthorized("Supplied API Key : 212Invalid is invalid"));

        //Test
        Exception exception = assertThrows(MovieHttpException.class, () -> {
            movieService.getMovieData("212Invalid", "Titanic");
        });

        //Verify
        String expectedMessage = "Supplied API Key : 212Invalid is invalid";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateMovieRating_existingMovie() {
        //prepare
        when(omDbClient.getMovieData("123bcd", "Titanic")).thenReturn(prepareMovieResult("9.5"));
        when(movieRatingRepository.findByMovieName("Titanic")).thenReturn(prepareMovieRatings(10, 9.3));
        when(movieRatingRepository.save(prepareMovieRatings(11, 9.3))).thenReturn(prepareMovieRatings(11, 9.3));

        //test
        RatingResponse response = movieService.updateMovieRating("123bcd", "Titanic", 9.7);

        assertNotNull(response);
        verify(movieRatingRepository).save(prepareMovieRatings(11, 9.3));
    }

    @Test
    void addMovieRating_Zero_BoxOffice(){
        //Prepare
        when(omDbClient.getMovieData("123bcd", "Titanic")).thenReturn(prepareMovieResultForBoxOffice("9.3"));
        when(movieRepository.findByMovieName("Titanic")).thenReturn(prepareMovie());
        when(movieRatingRepository.findByMovieName("Titanic")).thenReturn(null);
        MovieRatings updatedRating = MovieRatings.builder().ratingId(null).movieName("Titanic").rating(9.3).noOfRatings(1).boxOfficeValue(0).build();
        when(movieRatingRepository.save(updatedRating)).thenReturn(prepareMovieRatings(1, 9.3));

        //Test
        MovieResponse response = movieService.getMovieData("123bcd", "Titanic");

        //Verify
        assertNotNull(response);
        verify(movieRatingRepository).save(updatedRating);
    }

    @Test
    void getTop10BoxOfficeHits(){
        //prepare
        List<MovieRatings> top10Movies = prepareTop10();
        when(movieRatingRepository.findTop10ByOrderByBoxOfficeValueDesc()).thenReturn(top10Movies);

        //Test
        List<RatingResponse> response = movieService.getBoxOfficeTop10Movies();

        //verify
        assertNotNull(response);
        assertEquals(10, response.size());
        assertEquals("$9,999.00", response.get(0).getBoxOfficeValue());
        assertEquals("$9,998.00", response.get(1).getBoxOfficeValue());
        assertEquals("$9,997.00", response.get(2).getBoxOfficeValue());
    }

    private List<MovieRatings> prepareTop10() {
        return IntStream.range(1, 11).mapToObj(i -> prepareMovieRatings(i, "movie_"+i, i, 9.4, 10000-i)).collect(Collectors.toList());
    }

    private MovieRatings prepareMovieRatings(int ratingId, String movieName, int noOfRating, double rating, Integer boxOffice) {
        return MovieRatings.builder()
                .ratingId(ratingId).movieName(movieName).rating(rating).noOfRatings(noOfRating).boxOfficeValue(boxOffice).build();
    }

    private MovieRatings prepareMovieRatings(int noOfRating, double rating) {
        return MovieRatings.builder()
                .ratingId(2).movieName("Titanic").rating(rating).noOfRatings(noOfRating).boxOfficeValue(123344456).build();
    }

    private MovieResult prepareMovieResult(String imdbRating) {
        return new MovieResult()
                .withImdbID("123").withTitle("Titanic").withImdbRating(imdbRating)
                .withDirector("James").withBoxOffice("$123,344,456").withReleased("18 Jul 1998").withResponse("true");
    }

    private MovieResult prepareMovieResultForBoxOffice(String imdbRating) {
        return new MovieResult()
                .withImdbID("123").withTitle("Titanic").withImdbRating(imdbRating)
                .withDirector("James").withBoxOffice("N/A").withReleased("18 Jul 1998").withResponse("true");
    }

    private Movie prepareMovie() {
        return Movie.builder()
                .movieId(123).movieName("Titanic").category("Best Picture")
                .movieInfo("James Cameron and Jon Landau, Producers").winner("YES").year("1998").build();
    }

}
