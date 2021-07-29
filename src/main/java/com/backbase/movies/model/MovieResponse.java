package com.backbase.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Response class of the Movie
 *
 * @author Revathi
 */
@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class MovieResponse {
    /**
     * Field movieName
     */
    private String movieName;
    /**
     * Field releaseDate
     */
    private String releaseDate;
    /**
     * Field rating
     */
    private Double rating;
    /**
     * Field boxOfficeValue
     */
    private String boxOfficeValue;
    /**
     * Field isWonOscar
     */
    private String isWonOscar;
}
