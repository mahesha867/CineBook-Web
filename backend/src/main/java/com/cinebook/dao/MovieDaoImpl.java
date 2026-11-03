package com.cinebook.dao;

import com.cinebook.domain.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Plain JDBC Implementation of MovieDao using Spring JdbcTemplate.
 */
@Repository
public class MovieDaoImpl implements MovieDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovieDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Movie> MOVIE_ROW_MAPPER = (rs, rowNum) -> {
        Movie movie = new Movie();
        movie.setMovieId(rs.getInt("movie_id"));
        movie.setTitle(rs.getString("title"));
        movie.setGenre(rs.getString("genre"));
        movie.setDurationMins(rs.getInt("duration_mins"));
        movie.setRating(rs.getString("rating"));
        movie.setPosterUrl(rs.getString("poster_url"));
        movie.setDescription(rs.getString("description"));
        movie.setActive(rs.getBoolean("is_active"));
        movie.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return movie;
    };

    @Override
    public List<Movie> findAllActive() {
        String sql = "SELECT * FROM movies WHERE is_active = TRUE ORDER BY movie_id ASC";
        return jdbcTemplate.query(sql, MOVIE_ROW_MAPPER);
    }

    @Override
    public Optional<Movie> findById(int movieId) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try {
            Movie movie = jdbcTemplate.queryForObject(sql, MOVIE_ROW_MAPPER, movieId);
            return Optional.ofNullable(movie);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

