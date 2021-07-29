package com.backbase.movies.repository;

import com.backbase.movies.model.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to fetch movie from DB
 *
 * @author Revathi
 */
@Repository
public interface MovieRepository extends CrudRepository<Movie, Integer> {
    /**
     * Fetch the movie from db from tb_nominated_movies
     *
     * @param movieName movieName
     * @return Movie Object
     */
    Movie findByMovieName(String movieName);
}