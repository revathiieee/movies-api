package com.backbase.movies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate
 *
 * @author Revathi
 */
@Configuration
public class RestConfig {
    /**
     * Builds and returns RestTemplate Beam
     *
     * @return restTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
