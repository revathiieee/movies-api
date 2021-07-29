package com.backbase.movies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main Application
 *
 * @author Revathi
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.backbase.movies.config")
public class MoviesApiApplication {
    /**
     * Main method
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(MoviesApiApplication.class, args);
    }

}
