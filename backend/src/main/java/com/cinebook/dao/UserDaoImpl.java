package com.cinebook.dao;

import com.cinebook.domain.model.User;
import com.cinebook.domain.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRole(UserRole.valueOf(rs.getString("role").toUpperCase()));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        String sql = "SELECT * FROM users WHERE phone = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, phone));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User getOrCreateCustomer(String fullName, String email, String phone) {
        Optional<User> existing = findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        Optional<User> byPhone = findByPhone(phone);
        if (byPhone.isPresent()) {
            return byPhone.get();
        }

        String sql = "INSERT INTO users (full_name, email, phone, role) VALUES (?, ?, ?, 'CUSTOMER')";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, fullName != null ? fullName.trim() : "Valued Customer");
            ps.setString(2, email.trim());
            ps.setString(3, phone.trim());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        int newId = (generatedId != null) ? generatedId.intValue() : 0;
        return findById(newId).orElseThrow(() -> new IllegalStateException("Failed to retrieve created user."));
    }
}

