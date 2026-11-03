package com.cinebook.dao;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SeatDaoImpl implements SeatDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public SeatDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    private static final RowMapper<Seat> SEAT_ROW_MAPPER = (rs, rowNum) -> {
        Seat seat = new Seat();
        seat.setSeatId(rs.getInt("seat_id"));
        seat.setScreenId(rs.getInt("screen_id"));
        seat.setRowChar(rs.getString("row_char").charAt(0));
        seat.setSeatNum(rs.getInt("seat_num"));
        String tierStr = rs.getString("tier");
        seat.setTier(SeatType.valueOf(tierStr.toUpperCase()));
        return seat;
    };

    @Override
    public List<Seat> findByScreenId(int screenId) {
        String sql = "SELECT * FROM seats WHERE screen_id = ? ORDER BY row_char ASC, seat_num ASC";
        return jdbcTemplate.query(sql, SEAT_ROW_MAPPER, screenId);
    }

    @Override
    public List<Seat> findByIds(List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT * FROM seats WHERE seat_id IN (:ids) ORDER BY seat_id ASC";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", seatIds);
        return namedParameterJdbcTemplate.query(sql, params, SEAT_ROW_MAPPER);
    }

    @Override
    public Set<Integer> findBookedSeatIdsForShow(int showId) {
        String sql = "SELECT seat_id FROM booking_seats WHERE show_id = ?";
        List<Integer> list = jdbcTemplate.queryForList(sql, Integer.class, showId);
        return new HashSet<>(list);
    }

    @Override
    public List<Seat> findAvailableSeatsForShow(int showId) {
        String sql = """
            SELECT s.*
            FROM seats s
            JOIN shows sh ON s.screen_id = sh.screen_id
            WHERE sh.show_id = ?
              AND s.seat_id NOT IN (
                  SELECT bs.seat_id 
                  FROM booking_seats bs 
                  WHERE bs.show_id = ?
              )
              AND s.seat_id NOT IN (
                  SELECT h.seat_id 
                  FROM seat_holds h 
                  WHERE h.show_id = ? AND h.expires_at > NOW()
              )
            ORDER BY s.row_char ASC, s.seat_num ASC
        """;
        return jdbcTemplate.query(sql, SEAT_ROW_MAPPER, showId, showId, showId);
    }
}
