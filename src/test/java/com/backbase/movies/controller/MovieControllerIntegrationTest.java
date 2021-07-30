package com.backbase.movies.controller;

import com.backbase.movies.TestDataUtil;
import com.backbase.movies.model.MovieResponse;
import com.backbase.movies.model.RatingResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MovieControllerIntegrationTest {

    public HttpHeaders httpHeaders;
    private String staticURL = "http://localhost:";
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
    }

    @Test
    void getMovieData() throws Exception {

        String URI = "/api/movies/v1/bestPicture";

        httpHeaders.add("X-APIKEY", "130feada");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI)).queryParam("movieName", "Titanic");

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<MovieResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), GET, httpEntity, MovieResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void invalidApiKey() throws Exception {

        String URI = "/api/movies/v1/bestPicture";

        httpHeaders.add("X-APIKEY", "invalid-key");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI)).queryParam("movieName", "Titanic");

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), GET, httpEntity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertEquals("Supplied API Key : invalid-key is invalid", responseEntity.getBody());
    }

    @Test
    void invalidMovieName() {

        String URI = "/api/movies/v1/bestPicture";

        httpHeaders.add("X-APIKEY", "130feada");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI)).queryParam("movieName", "invalidMovie");

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), GET, httpEntity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEquals("MovieName : invalidMovie does not exist.", responseEntity.getBody());
    }

    @Test
    void emptyMovieName() {

        String URI = "/api/movies/v1/bestPicture";

        httpHeaders.add("X-APIKEY", "130feada");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI)).queryParam("movieName", "");

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), GET, httpEntity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertTrue(responseEntity.getBody().contains("Movie Name should not be empty"));
    }

    @Test
    void errorUpdateRating() {

        String URI = "/api/movies/v1/rating";

        MultiValueMap params = new LinkedMultiValueMap();
        params.add("movieName", "Titanic");
        params.add("rating","12");

        httpHeaders.add("X-APIKEY", "130feada");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI))
                .queryParams(params);

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), PUT, httpEntity, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertTrue(responseEntity.getBody().contains("Rating should be between 1 to 10"));
    }


    @Test
    void updateRating() throws Exception {

        String URI = "/api/movies/v1/rating";
        String jsonOutput = this.convertToJson(TestDataUtil.formOmdbRatingResponse());

        MultiValueMap params = new LinkedMultiValueMap();
        params.add("movieName", "Titanic");
        params.add("rating","8.4");

        httpHeaders.add("X-APIKEY", "130feada");
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI))
                                      .queryParams(params);

        ResponseEntity<RatingResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), PUT, httpEntity, RatingResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONAssert.assertEquals(jsonOutput, this.convertToJson(responseEntity.getBody()), false);
    }

    @Test
    void getBoxOfficeTop10Movies() throws Exception {

        String URI = "/api/movies/v1/boxOffice";
        String jsonOutput = this.convertToJson(TestDataUtil.formTop10BoxOfficeResponse());

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCompleteEndPoint(URI));

        ResponseEntity<List> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), GET, null, List.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String convertToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private String getCompleteEndPoint(String URI) {
        return staticURL + port + URI;
    }
}
