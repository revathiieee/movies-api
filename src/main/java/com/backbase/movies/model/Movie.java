package com.backbase.movies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Movie Entity
 *
 * @author Revathi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_nominated_movies")
@Builder
@Accessors(chain = true)
public class Movie {
    /**
     * Field movieId
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;
    /**
     * Field movieName
     */
    @Column(name = "movie_name")
    private String movieName;
    /**
     * Field movieInfo
     */
    @Column(name = "movie_info")
    private String movieInfo;
    /**
     * Field Category
     */
    @Column(name = "category")
    private String category;
    /**
     * Field winner
     */
    @Column(name = "is_winner")
    private String winner;
    /**
     * Field year
     */
    @Column(name = "year")
    private String year;
}
