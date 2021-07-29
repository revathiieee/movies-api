package com.backbase.movies.repository;

import com.backbase.movies.model.MovieRatings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository to find movie Ratings
 *
 * @author Revathi
 */
@Repository
public interface MovieRatingRepository extends CrudRepository<MovieRatings, Integer> {
    /**
     * Fetch the movie from db from tb_movie_rating
     *
     * @param movieName movieName
     * @return MovieRatings object
     */
    MovieRatings findByMovieName(String movieName);

    /**
     * Fetches Top 10 movies by box office Value from tb_movie_rating
     *
     * @return list of movieRatings object
     */
    List<MovieRatings> findTop10ByOrderByBoxOfficeValueDesc();
}
