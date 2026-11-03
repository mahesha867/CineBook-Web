package com.cinebook.dao;

import com.cinebook.domain.model.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ScreenDaoImpl implements ScreenDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ScreenDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Screen> SCREEN_ROW_MAPPER = (rs, rowNum) -> {
        Screen screen = new Screen();
        screen.setScreenId(rs.getInt("screen_id"));
        screen.setScreenName(rs.getString("screen_name"));
        screen.setTotalRows(rs.getInt("total_rows"));
        screen.setSeatsPerRow(rs.getInt("seats_per_row"));
        return screen;
    };

    @Override
    public Optional<Screen> findById(int screenId) {
        String sql = "SELECT * FROM screens WHERE screen_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, SCREEN_ROW_MAPPER, screenId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Screen> findAll() {
        String sql = "SELECT * FROM screens ORDER BY screen_id ASC";
        return jdbcTemplate.query(sql, SCREEN_ROW_MAPPER);
    }
}

