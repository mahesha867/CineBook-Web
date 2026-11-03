package com.cinebook.dao;

import com.cinebook.domain.model.WaitlistEntry;
import com.cinebook.domain.model.WaitlistStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class WaitlistDaoImpl implements WaitlistDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WaitlistDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<WaitlistEntry> WAITLIST_ROW_MAPPER = (rs, rowNum) -> {
        WaitlistEntry w = new WaitlistEntry();
        w.setWaitlistId(rs.getInt("waitlist_id"));
        w.setShowId(rs.getInt("show_id"));
        w.setUserId(rs.getInt("user_id"));
        w.setSeatsRequested(rs.getInt("seats_requested"));
        w.setPreferredTier(rs.getString("preferred_tier"));
        w.setStatus(WaitlistStatus.valueOf(rs.getString("status").toUpperCase()));
        w.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp promotedTs = rs.getTimestamp("promoted_at");
        if (promotedTs != null) {
            w.setPromotedAt(promotedTs.toLocalDateTime());
        }
        w.setCustomerName(rs.getString("customer_name"));
        w.setCustomerEmail(rs.getString("customer_email"));
        w.setMovieTitle(rs.getString("movie_title"));
        return w;
    };

    @Override
    public int insertWaitlist(WaitlistEntry entry) {
        String sql = "INSERT INTO waitlist (show_id, user_id, seats_requested, preferred_tier, status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, entry.getShowId());
            ps.setInt(2, entry.getUserId());
            ps.setInt(3, entry.getSeatsRequested());
            ps.setString(4, entry.getPreferredTier());
            ps.setString(5, entry.getStatus().name());
            return ps;
        }, keyHolder);

        Number k = keyHolder.getKey();
        return (k != null) ? k.intValue() : 0;
    }

    @Override
    public Optional<WaitlistEntry> findFirstWaiting(int showId) {
        String sql = """
            SELECT w.*, u.full_name AS customer_name, u.email AS customer_email, m.title AS movie_title
            FROM waitlist w
            JOIN users u ON w.user_id = u.user_id
            JOIN shows s ON w.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            WHERE w.show_id = ? AND w.status = 'WAITING'
            ORDER BY w.created_at ASC
            LIMIT 1
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, WAITLIST_ROW_MAPPER, showId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateStatus(int waitlistId, WaitlistStatus status, LocalDateTime promotedAt) {
        String sql = "UPDATE waitlist SET status = ?, promoted_at = ? WHERE waitlist_id = ?";
        jdbcTemplate.update(sql, status.name(), promotedAt != null ? Timestamp.valueOf(promotedAt) : null, waitlistId);
    }

    @Override
    public List<WaitlistEntry> findByShowId(int showId) {
        String sql = """
            SELECT w.*, u.full_name AS customer_name, u.email AS customer_email, m.title AS movie_title
            FROM waitlist w
            JOIN users u ON w.user_id = u.user_id
            JOIN shows s ON w.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            WHERE w.show_id = ?
            ORDER BY w.created_at ASC
        """;
        return jdbcTemplate.query(sql, WAITLIST_ROW_MAPPER, showId);
    }

    @Override
    public Optional<WaitlistEntry> findByUserAndShow(int userId, int showId) {
        String sql = """
            SELECT w.*, u.full_name AS customer_name, u.email AS customer_email, m.title AS movie_title
            FROM waitlist w
            JOIN users u ON w.user_id = u.user_id
            JOIN shows s ON w.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            WHERE w.user_id = ? AND w.show_id = ?
            ORDER BY w.created_at DESC
            LIMIT 1
        """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, WAITLIST_ROW_MAPPER, userId, showId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

