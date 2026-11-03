package com.cinebook.dao;

import com.cinebook.domain.model.SeatHold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.*;

@Repository
public class SeatHoldDaoImpl implements SeatHoldDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SeatHoldDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    private static final RowMapper<SeatHold> SEAT_HOLD_ROW_MAPPER = (rs, rowNum) -> {
        SeatHold hold = new SeatHold();
        hold.setHoldId(rs.getInt("hold_id"));
        hold.setShowId(rs.getInt("show_id"));
        hold.setSeatId(rs.getInt("seat_id"));
        hold.setSessionId(rs.getString("session_id"));
        hold.setHeldAt(rs.getTimestamp("held_at").toLocalDateTime());
        hold.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        return hold;
    };

    @Override
    public void holdSeats(int showId, List<Integer> seatIds, String sessionId, int durationMinutes) {
        if (seatIds == null || seatIds.isEmpty()) return;

        String sql = """
            INSERT INTO seat_holds (show_id, seat_id, session_id, held_at, expires_at)
            VALUES (?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? MINUTE))
            ON DUPLICATE KEY UPDATE
                session_id = VALUES(session_id),
                held_at = NOW(),
                expires_at = DATE_ADD(NOW(), INTERVAL ? MINUTE)
        """;

        List<Object[]> batchArgs = new ArrayList<>();
        for (Integer seatId : seatIds) {
            batchArgs.add(new Object[]{showId, seatId, sessionId, durationMinutes, durationMinutes});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public Set<Integer> findActiveHeldSeatIdsForShow(int showId) {
        String sql = "SELECT seat_id FROM seat_holds WHERE show_id = ? AND expires_at > NOW()";
        List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, showId);
        return new HashSet<>(list);
    }

    @Override
    public Map<Integer, SeatHold> findActiveHoldsMapForShow(int showId) {
        String sql = "SELECT * FROM seat_holds WHERE show_id = ? AND expires_at > NOW()";
        List<SeatHold> list = jdbcTemplate.query(sql, SEAT_HOLD_ROW_MAPPER, showId);
        Map<Integer, SeatHold> map = new HashMap<>();
        for (SeatHold h : list) {
            map.put(h.getSeatId(), h);
        }
        return map;
    }

    @Override
    public boolean areAllSeatsHeldBySession(int showId, List<Integer> seatIds, String sessionId) {
        if (seatIds == null || seatIds.isEmpty()) return true;

        String sql = """
            SELECT COUNT(*) FROM seat_holds
            WHERE show_id = :showId 
              AND seat_id IN (:seatIds) 
              AND session_id = :sessionId 
              AND expires_at > NOW()
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("showId", showId)
                .addValue("seatIds", seatIds)
                .addValue("sessionId", sessionId);

        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count == seatIds.size();
    }

    @Override
    public void releaseHolds(int showId, List<Integer> seatIds, String sessionId) {
        if (seatIds == null || seatIds.isEmpty()) return;
        String sql = "DELETE FROM seat_holds WHERE show_id = :showId AND seat_id IN (:seatIds) AND session_id = :sessionId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("showId", showId)
                .addValue("seatIds", seatIds)
                .addValue("sessionId", sessionId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public int releaseExpiredHolds() {
        // DBMS Showcase: Invokes MySQL Stored Procedure sp_release_expired_holds
        return jdbcTemplate.execute((java.sql.Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{CALL sp_release_expired_holds(?)}")) {
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();
                return cs.getInt(1);
            }
        });
    }
}

