package com.cinebook.dao;

import com.cinebook.domain.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponDaoImpl implements CouponDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CouponDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Coupon> COUPON_ROW_MAPPER = (rs, rowNum) -> {
        Coupon c = new Coupon();
        c.setCouponCode(rs.getString("coupon_code"));
        c.setDiscountPercent(rs.getBigDecimal("discount_percent"));
        c.setValidFrom(rs.getTimestamp("valid_from").toLocalDateTime());
        c.setValidUntil(rs.getTimestamp("valid_until").toLocalDateTime());
        c.setActive(rs.getBoolean("is_active"));
        return c;
    };

    @Override
    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        String sql = "SELECT * FROM coupons WHERE coupon_code = ? AND is_active = TRUE";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, COUPON_ROW_MAPPER, code.trim().toUpperCase()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

