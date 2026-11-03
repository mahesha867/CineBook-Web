package com.cinebook.dao;

import com.cinebook.domain.model.Movie;

import java.util.List;
import java.util.Optional;

/**
 * DAO Pattern: MovieDao Interface.
 * 
 * Demonstrates plain JDBC data abstraction without ORM.
 */
public interface MovieDao {
    List<Movie> findAllActive();
    Optional<Movie> findById(int movieId);
}

