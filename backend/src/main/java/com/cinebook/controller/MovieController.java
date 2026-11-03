package com.cinebook.controller;

import com.cinebook.domain.model.Movie;
import com.cinebook.domain.model.Show;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.service.MovieService;
import com.cinebook.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final ShowService showService;

    @Autowired
    public MovieController(MovieService movieService, ShowService showService) {
        this.movieService = movieService;
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getAllActiveMovies()));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable int movieId) {
        return movieService.getMovieById(movieId)
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{movieId}/shows")
    public ResponseEntity<ApiResponse<List<Show>>> getUpcomingShows(@PathVariable int movieId) {
        return ResponseEntity.ok(ApiResponse.ok(showService.getUpcomingShowsByMovie(movieId)));
    }
}

