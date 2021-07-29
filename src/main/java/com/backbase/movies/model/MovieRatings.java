package com.backbase.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * MovieRatings Entity
 *
 * @author Revathi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_movie_rating")
@Builder
@Accessors(chain = true)
public class MovieRatings {

    /**
     * Field ratingId
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;
    /**
     * Field movieName
     */
    @Column(name = "movie_name")
    private String movieName;
    /**
     * Field rating
     */
    @Column(name = "rating")
    private Double rating;
    /**
     * Field boxOfficeValue
     */
    @Column(name = "box_office_value")
    private Integer boxOfficeValue;
    /**
     * Field noOfRatings
     */
    @Column(name = "no_of_ratings")
    private Integer noOfRatings;

}
