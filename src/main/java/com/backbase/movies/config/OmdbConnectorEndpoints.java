package com.backbase.movies.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration to fetch omdb endpoint
 *
 * @author Revathi
 */
@Data
@ConfigurationProperties("backbase.movies.omdb")
public class OmdbConnectorEndpoints {
    /**
     * Omdb endpoint
     */
    private String endPoint;
}
