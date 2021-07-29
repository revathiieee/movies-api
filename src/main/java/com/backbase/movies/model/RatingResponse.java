package com.backbase.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Response class of the rating
 *
 * @author Revathi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class RatingResponse {
    /**
     * Field ratingId
     */
    private Integer ratingId;
    /**
     * Field movieName
     */
    private String movieName;
    /**
     * Field rating
     */
    private Double rating;
    /**
     * Field boxOfficeValue
     */
    private String boxOfficeValue;
}
