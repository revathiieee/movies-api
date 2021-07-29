package com.backbase.movies.client;

import com.backbase.movies.config.OmdbConnectorEndpoints;
import com.backbase.movies.exception.MovieHttpException;
import com.backbase.movies.model.generated.MovieResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

/**
 * Handles the communication to OMDB API
 *
 * @author Revathi
 */
@Service
@Slf4j
public class OMDbClient {
    /**
     * Field restTemplate
     */
    private final RestTemplate restTemplate;
    /**
     * Field connector Endpoints
     */
    private final OmdbConnectorEndpoints omdbConnectorEndpoints;

    /**
     * Constructor instantiates with RestTemplate and Endpoints
     *
     * @param restTemplate           restTemplate
     * @param omdbConnectorEndpoints omdbEndpoints
     */
    public OMDbClient(RestTemplate restTemplate, OmdbConnectorEndpoints omdbConnectorEndpoints) {
        this.restTemplate = restTemplate;
        this.omdbConnectorEndpoints = omdbConnectorEndpoints;
    }

    /**
     * Validate ApiKey. If not valid throws MovieHttpException with 401 status and message
     * Validate movieName. If not valid throws MovieHttpException with 404 status and message
     *
     * @param apiKey    apiKey
     * @param movieName movie
     * @return MovieResult valid Json based on omdb data
     * @throws MovieHttpException with http status and message
     */
    public MovieResult getMovieData(String apiKey, String movieName) throws MovieHttpException {
        Optional<MovieResult> movieResult = Optional.empty();
        try {
            movieResult = Optional.ofNullable(restTemplate.getForObject(buildUrl(apiKey, movieName), MovieResult.class));
            if (movieResult.isPresent() && movieResult.get().getResponse().equalsIgnoreCase("false")) {
                throw MovieHttpException.notFound("MovieName : " + movieName + " does not exist.");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                log.error("Supplied API Key is invalid {}", apiKey);
                throw MovieHttpException.unAuthorized("Supplied API Key : " + apiKey + " is invalid");
            } else if (e.getStatusCode().is4xxClientError()) {
                throw MovieHttpException.badRequest("Client Error");
            }
        } catch (RestClientException e) {
            log.error("Problem getting movieData: " + e.getMessage());
            throw MovieHttpException.serverError("Internal Server Error");
        }

        return movieResult.get();
    }

    private URI buildUrl(String apiKey, String movieName) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(omdbConnectorEndpoints.getEndPoint()).query("apiKey={apiKey}").query("t={movieName}");
        return uriComponentsBuilder.build(apiKey, movieName);
    }
}
