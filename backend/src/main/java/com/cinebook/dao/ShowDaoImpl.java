package com.cinebook.dao;

import com.cinebook.domain.model.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShowDaoImpl implements ShowDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShowDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Show> SHOW_ROW_MAPPER = (rs, rowNum) -> {
        Show show = new Show();
        show.setShowId(rs.getInt("show_id"));
        show.setMovieId(rs.getInt("movie_id"));
        show.setScreenId(rs.getInt("screen_id"));
        show.setShowTime(rs.getTimestamp("show_time").toLocalDateTime());
        show.setBasePriceMultiplier(rs.getBigDecimal("base_price_multiplier"));
        show.setCancelled(rs.getBoolean("is_cancelled"));
        show.setMovieTitle(rs.getString("movie_title"));
        show.setScreenName(rs.getString("screen_name"));
        return show;
    };

    @Override
    public Optional<Show> findById(int showId) {
        String sql = """
            SELECT s.*, m.title AS movie_title, sc.screen_name
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            WHERE s.show_id = ?
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, SHOW_ROW_MAPPER, showId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Show> findUpcomingByMovieId(int movieId) {
        String sql = """
            SELECT s.*, m.title AS movie_title, sc.screen_name
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            WHERE s.movie_id = ? AND s.is_cancelled = FALSE AND s.show_time >= NOW() - INTERVAL 2 HOUR
            ORDER BY s.show_time ASC
        """;
        return jdbcTemplate.query(sql, SHOW_ROW_MAPPER, movieId);
    }

    @Override
    public List<Show> findAllUpcoming() {
        String sql = """
            SELECT s.*, m.title AS movie_title, sc.screen_name
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            WHERE s.is_cancelled = FALSE AND s.show_time >= NOW() - INTERVAL 2 HOUR
            ORDER BY s.show_time ASC
        """;
        return jdbcTemplate.query(sql, SHOW_ROW_MAPPER);
    }
}

