package com.cinebook.service;

import com.cinebook.dao.MovieDao;
import com.cinebook.domain.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieDao movieDao;

    @Autowired
    public MovieService(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    public List<Movie> getAllActiveMovies() {
        return movieDao.findAllActive();
    }

    public Optional<Movie> getMovieById(int movieId) {
        return movieDao.findById(movieId);
    }
}

