package com.backbase.movies.controller;

import com.backbase.movies.TestDataUtil;
import com.backbase.movies.exception.MovieControllerExceptionAdvice;
import com.backbase.movies.exception.MovieHttpException;
import com.backbase.movies.model.MovieResponse;
import com.backbase.movies.model.RatingResponse;
import com.backbase.movies.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
class MoviesControllerTest {

    private MockMvc mvc;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MoviesController moviesController;

    private JacksonTester<MovieResponse> movieResponse;

    private JacksonTester<RatingResponse> ratingResponse;

    private JacksonTester<List<RatingResponse>> boxOfficeList;


    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(moviesController)
                .setControllerAdvice(new MovieControllerExceptionAdvice())
                .build();
    }

    @Test
    void getMovieData() throws Exception {

        when(movieService.getMovieData("test-api-key", "Titanic")).thenReturn(TestDataUtil.formMovieResponse());

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/bestPicture").header("X-APIKEY","test-api-key").queryParam("movieName", "Titanic").contentType(MediaType.APPLICATION_JSON).content(
                        movieResponse.write(TestDataUtil.formMovieResponse()).getJson()
                )).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                movieResponse.write(TestDataUtil.formMovieResponse()).getJson()
        );
        assertTrue(response.getContentAsString().contains("Titanic"));
    }

    @Test
    void inValidApiKey() throws Exception {

        when(movieService.getMovieData("invalid-api-key", "Titanic"))
                .thenThrow(MovieHttpException.unAuthorized("Supplied API Key : invalid-api-key is invalid"));

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/bestPicture").header("X-APIKEY","invalid-api-key").queryParam("movieName", "Titanic"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                "Supplied API Key : invalid-api-key is invalid"
        );
    }

    @Test
    void inValidMovieName() throws Exception {

        when(movieService.getMovieData("test-api-key", "invalidMovie"))
                .thenThrow(MovieHttpException.notFound("MovieName : invalidMovie does not exist."));

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/bestPicture").header("X-APIKEY","test-api-key")
                        .queryParam("movieName", "invalidMovie")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                "MovieName : invalidMovie does not exist."
        );
    }

    @Test
    void serverError() throws Exception {

        when(movieService.getMovieData("test-api-key", "Titanic")).thenThrow(MovieHttpException.serverError("Internal Server Error"));

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/bestPicture").header("X-APIKEY","test-api-key").queryParam("movieName", "Titanic"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                "Internal Server Error"
        );
    }

    @Test
    void badError() throws Exception {

        when(movieService.getMovieData("test-api-key", "Titanic")).thenThrow(MovieHttpException.badRequest("Client Error"));

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/bestPicture").header("X-APIKEY","test-api-key").queryParam("movieName", "Titanic"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                "Client Error"
        );
    }

    @Test
    void updateMovieRating() throws Exception {

        when(movieService.updateMovieRating("test-api-key", "Titanic", 8.4)).thenReturn(TestDataUtil.formRatingResponse());

        MultiValueMap params = new LinkedMultiValueMap();
        params.add("movieName", "Titanic");
        params.add("rating","8.4");

        MockHttpServletResponse response = mvc.perform(
                put("/api/movies/v1/rating").header("X-APIKEY","test-api-key")
                        .queryParams(params).contentType(MediaType.APPLICATION_JSON).content(
                        ratingResponse.write(TestDataUtil.formRatingResponse()).getJson()
                )).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                ratingResponse.write(TestDataUtil.formRatingResponse()).getJson()
        );
        assertTrue(response.getContentAsString().contains("Titanic"));
    }

    @Test
    void getTop10BoxOfficeData() throws Exception {

        when(movieService.getBoxOfficeTop10Movies()).thenReturn(TestDataUtil.formTop10BoxOfficeResponse());

        MockHttpServletResponse response = mvc.perform(
                get("/api/movies/v1/boxOffice")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertNotNull(response.getContentAsString());
        assertThat(response.getContentAsString()).isEqualTo(
                boxOfficeList.write(TestDataUtil.formTop10BoxOfficeResponse()).getJson()
        );
        assertTrue(response.getContentAsString().contains("Toy Story 3"));
    }
}
