package com.backbase.movies.client;

import com.backbase.movies.TestDataUtil;
import com.backbase.movies.config.OmdbConnectorEndpoints;
import com.backbase.movies.exception.MovieHttpException;
import com.backbase.movies.model.generated.MovieResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class OmdbClientTest {

    private MockRestServiceServer omdbServer;
    private OMDbClient omDbClient;
    private RestTemplate restTemplate = new RestTemplate();

    private static String json(String singleQuotedJson) {
        return singleQuotedJson.replaceAll("'", "\"");
    }

    @BeforeEach
    public void setUp() {
        omdbServer = MockRestServiceServer.createServer(restTemplate);
        OmdbConnectorEndpoints omdbConnectorEndpoints = new OmdbConnectorEndpoints();
        omdbConnectorEndpoints.setEndPoint("https://www.testomdb/?i=abc123");

        omDbClient = new OMDbClient(restTemplate, omdbConnectorEndpoints);
    }

    @Test
    void getMovieData() throws IOException {
        // Prepare
        omdbServer.expect(requestTo("https://www.testomdb/?i=abc123&apiKey=test-api-key&t=Titanic"))
                .andExpect(method(GET))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(TestDataUtil.formMovieResult()), MediaType.APPLICATION_JSON));

        // Test
        MovieResult response = omDbClient.getMovieData("test-api-key", "Titanic");

        // Verify
        assertNotNull(response);
        assertEquals("Titanic", response.getTitle());
        assertEquals("1997", response.getYear());
        assertEquals("$389,813,101", response.getBoxOffice());
    }

    @Test
    void invalidApiKey() throws IOException {
        // Prepare
        omdbServer.expect(requestTo("https://www.testomdb/?i=abc123&apiKey=invalidApiKey&t=Titanic"))
                .andExpect(method(GET))
                .andRespond(withUnauthorizedRequest());

        // Test
        MovieHttpException exception = assertThrows(MovieHttpException.class, () -> {
            omDbClient.getMovieData("invalidApiKey", "Titanic");
        });

        //Verify
        String expectedMessage = "Supplied API Key : invalidApiKey is invalid";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertEquals(401, exception.getHttpStatus());
    }

    @Test
    void invalidMovieName() throws IOException {
        // Prepare
        omdbServer.expect(requestTo("https://www.testomdb/?i=abc123&apiKey=test-api-key&t=invalidMovieName"))
                .andExpect(method(GET))
                .andRespond(withSuccess(new ObjectMapper().writeValueAsString(TestDataUtil.formInvalidMovieNameResponse()), MediaType.APPLICATION_JSON));

        // Test
        MovieHttpException exception = assertThrows(MovieHttpException.class, () -> {
            omDbClient.getMovieData("test-api-key", "invalidMovieName");
        });

        //Verify
        String expectedMessage = "MovieName : invalidMovieName does not exist.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertEquals(404, exception.getHttpStatus());
    }

    @Test
    void serverError() {
        // Prepare
        omdbServer.expect(requestTo("https://www.testomdb/?i=abc123&apiKey=test-api-key&t=invalidMovieName"))
                .andExpect(method(GET))
                .andRespond(withServerError());

        // Test
        MovieHttpException exception = assertThrows(MovieHttpException.class, () -> {
            omDbClient.getMovieData("test-api-key", "invalidMovieName");
        });

        //Verify
        String expectedMessage = "Internal Server Error";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertEquals(500, exception.getHttpStatus());
    }

    @Test
    void clientError() {
        // Prepare
        omdbServer.expect(requestTo("https://www.testomdb/?i=abc123&apiKey=test-api-key&t=invalidMovieName"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        // Test
        MovieHttpException exception = assertThrows(MovieHttpException.class, () -> {
            omDbClient.getMovieData("test-api-key", "invalidMovieName");
        });

        //Verify
        String expectedMessage = "Client Error";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertEquals(400, exception.getHttpStatus());
    }


}
