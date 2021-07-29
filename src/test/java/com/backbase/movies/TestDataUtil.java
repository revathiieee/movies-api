package com.backbase.movies;

import com.backbase.movies.model.MovieResponse;
import com.backbase.movies.model.RatingResponse;
import com.backbase.movies.model.generated.MovieResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestDataUtil {

    public static MovieResult formMovieResult() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MovieResult movieResult = objectMapper.readValue(new File("src/test/resources/movieResult.json"), MovieResult.class);
        return movieResult;
    }

    public static MovieResult formInvalidMovieNameResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MovieResult movieResult = objectMapper.readValue(new File("src/test/resources/invalidMovieName.json"), MovieResult.class);
        return movieResult;
    }

    public static MovieResponse formMovieResponse() throws IOException {
        return MovieResponse.builder()
                .movieName("Titanic").releaseDate("18 Jul 1998")
                .rating(9.0).boxOfficeValue("$123,344,456").isWonOscar("YES").build();
    }

    public static RatingResponse formRatingResponse() {
        return RatingResponse.builder()
                .ratingId(1)
                .movieName("Titanic").rating(8.4)
                .boxOfficeValue("$123,344,456")
                .build();
    }

    public static RatingResponse formOmdbRatingResponse() {
        return RatingResponse.builder()
                .ratingId(25)
                .movieName("Titanic").rating(8.4)
                .boxOfficeValue("$659,363,944.00")
                .build();
    }

    public static List<RatingResponse> formTop10BoxOfficeResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RatingResponse[] ratingResponses =
                objectMapper.readValue(new File("src/test/resources/top10Movies.json"), RatingResponse[].class);
        return Arrays.asList(ratingResponses);
    }
}
