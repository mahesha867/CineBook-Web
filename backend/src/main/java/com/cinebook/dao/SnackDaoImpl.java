package com.cinebook.dao;

import com.cinebook.domain.model.Snack;
import com.cinebook.domain.model.SnackCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class SnackDaoImpl implements SnackDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SnackDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    private static final RowMapper<Snack> SNACK_ROW_MAPPER = (rs, rowNum) -> {
        Snack snack = new Snack();
        snack.setSnackId(rs.getString("snack_id"));
        snack.setName(rs.getString("name"));
        snack.setPrice(rs.getBigDecimal("price"));
        snack.setCategory(SnackCategory.valueOf(rs.getString("category").toUpperCase()));
        snack.setAvailable(rs.getBoolean("is_available"));
        return snack;
    };

    @Override
    public List<Snack> findAllAvailable() {
        String sql = "SELECT * FROM snacks WHERE is_available = TRUE ORDER BY category ASC, price ASC";
        return jdbcTemplate.query(sql, SNACK_ROW_MAPPER);
    }

    @Override
    public Optional<Snack> findById(String snackId) {
        String sql = "SELECT * FROM snacks WHERE snack_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, SNACK_ROW_MAPPER, snackId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Snack> findByIds(List<String> snackIds) {
        if (snackIds == null || snackIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT * FROM snacks WHERE snack_id IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", snackIds);
        return namedParameterJdbcTemplate.query(sql, params, SNACK_ROW_MAPPER);
    }

    @Override
    public void updatePrice(String snackId, BigDecimal newPrice) {
        String sql = "UPDATE snacks SET price = ? WHERE snack_id = ?";
        jdbcTemplate.update(sql, newPrice, snackId);
    }
}

