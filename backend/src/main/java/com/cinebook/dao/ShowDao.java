package com.cinebook.dao;

import com.cinebook.domain.model.Show;

import java.util.List;
import java.util.Optional;

public interface ShowDao {
    Optional<Show> findById(int showId);
    List<Show> findUpcomingByMovieId(int movieId);
    List<Show> findAllUpcoming();
}

