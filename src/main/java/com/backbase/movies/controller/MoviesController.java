package com.backbase.movies.controller;

import com.backbase.movies.exception.MovieHttpException;
import com.backbase.movies.model.MovieResponse;
import com.backbase.movies.model.RatingResponse;
import com.backbase.movies.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * Rest Controller to handle movies data
 *
 * @author Revathi
 */
@RestController
@RequestMapping("/api/movies/v1")
@Slf4j
@Validated
public class MoviesController {
    /**
     * Field Movie Service
     */
    private final MovieService movieService;

    /**
     * Constructor instantiation with MovieService
     *
     * @param movieService movieService
     */
    public MoviesController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Retrieve whether the given picture won the oscar or not by validating the given api key and movieName
     *
     * @param apiKey    apiKeyValue
     * @param movieName movieName
     * @return MovieResponse object
     * @throws MovieHttpException with Http status and Message
     */
    @GetMapping(path = "/bestPicture")
    @Operation(method = "GET", summary = "Get Movie Data", description = "To Know whether picture won Best Picture")
    public ResponseEntity<MovieResponse> getMovieData(@RequestHeader(value = "X-APIKEY") String apiKey,
                                                      @Parameter(name = "movieName") @RequestParam(value = "movieName") @NotBlank(message = "Movie Name should not be empty") String movieName)
            throws MovieHttpException {
        return ResponseEntity.status(OK).body(movieService.getMovieData(apiKey, movieName));
    }

    /**
     * User can update the moving rating by giving valid apiKey and movieName
     *
     * @param apiKey    apiKeyValue
     * @param movieName movieName
     * @param rating    userPreferedRating
     * @return RatingResponse object
     * @throws MovieHttpException with Http status and Message
     */
    @PutMapping(path = "/rating")
    @Operation(method = "PUT", summary = "Update Movie Rating", description = "Give Rating for a Movie")
    public ResponseEntity<RatingResponse> updateMovieRating(@RequestHeader(value = "X-APIKEY") String apiKey,
                                                            @Parameter(name = "movieName") @RequestParam(value = "movieName") String movieName,
                                                            @Parameter(name = "rating") @RequestParam(value = "rating") @Min(value = 1, message = "Rating should be minimum 1") @Max(value = 10, message = "Rating should be between 1 to 10") Double rating)
                                                            throws MovieHttpException {
        return ResponseEntity.status(OK).body(movieService.updateMovieRating(apiKey, movieName, rating));
    }

    /**
     * Retrives Top 10 Box Office Movies from DB
     *
     * @return List of RatingResponse objects
     */
    @GetMapping(path = "/boxOffice")
    @Operation(method = "GET", summary = "Get Top 10 BoxOffice Movie Data", description = "To Know top 10 box office movies")
    public ResponseEntity<List<RatingResponse>> getTop10BoxOfficeMovies() {
        return ResponseEntity.status(OK).body(movieService.getBoxOfficeTop10Movies());
    }
}
