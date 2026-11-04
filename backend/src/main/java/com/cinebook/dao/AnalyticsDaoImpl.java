package com.cinebook.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plain JDBC Implementation of AnalyticsDao querying custom MySQL Views, Window Functions, and Relational Joins.
 */
@Repository
public class AnalyticsDaoImpl implements AnalyticsDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AnalyticsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> getKpiSummary() {
        String sql = """
            SELECT 
                COUNT(*) AS total_bookings,
                SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) AS confirmed_bookings,
                SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_bookings,
                COALESCE(SUM(CASE WHEN status = 'CONFIRMED' THEN final_total ELSE (final_total - refund_amount) END), 0.00) AS net_revenue,
                COALESCE(SUM(ticket_subtotal - ticket_discount), 0.00) AS total_ticket_revenue,
                COALESCE(SUM(snack_subtotal), 0.00) AS total_snack_revenue,
                COALESCE(SUM(refund_amount), 0.00) AS total_refunds_issued
            FROM bookings
        """;
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        Map<String, Object> result = new HashMap<>(map);

        // Compute overall cinema occupancy percentage
        Double avgOccupancy = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(occupancy_percent), 0.0) FROM v_show_occupancy", 
                Double.class
        );
        result.put("average_occupancy_percent", avgOccupancy != null ? avgOccupancy : 0.0);

        return result;
    }

    @Override
    public List<Map<String, Object>> getMovieRevenueStats() {
        // Querying Database View: v_movie_revenue_analytics
        String sql = "SELECT * FROM v_movie_revenue_analytics ORDER BY net_revenue DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getShowOccupancyStats() {
        // Querying Database View: v_show_occupancy
        String sql = "SELECT * FROM v_show_occupancy ORDER BY show_time ASC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getTopSellingSnacks() {
        // Querying Database View: v_top_selling_snacks (utilizing SQL window function DENSE_RANK())
        String sql = "SELECT * FROM v_top_selling_snacks ORDER BY revenue_rank ASC, total_quantity_sold DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getPeakBookingHours() {
        String sql = """
            SELECT 
                HOUR(booking_time) AS booking_hour,
                COUNT(*) AS total_bookings,
                SUM(final_total) AS hourly_revenue
            FROM bookings
            GROUP BY HOUR(booking_time)
            ORDER BY booking_hour ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getAdminShowsOverview() {
        String sql = """
            SELECT 
                s.show_id,
                s.movie_id,
                m.title AS movie_title,
                m.poster_url,
                m.genre,
                m.duration_mins,
                m.rating,
                s.screen_id,
                sc.screen_name,
                s.show_time,
                s.base_price_multiplier,
                s.is_cancelled,
                (sc.total_rows * sc.seats_per_row) AS total_capacity,
                COALESCE(booked_data.booked_seats, 0) AS booked_seats,
                COALESCE(held_data.held_seats, 0) AS held_seats,
                ROUND((COALESCE(booked_data.booked_seats, 0) / (sc.total_rows * sc.seats_per_row)) * 100, 2) AS occupancy_percent
            FROM shows s
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            LEFT JOIN (
                SELECT b.show_id, COUNT(bs.seat_id) AS booked_seats
                FROM bookings b
                JOIN booking_seats bs ON b.booking_id = bs.booking_id
                WHERE b.status = 'CONFIRMED'
                GROUP BY b.show_id
            ) booked_data ON s.show_id = booked_data.show_id
            LEFT JOIN (
                SELECT sh.show_id, COUNT(sh.seat_id) AS held_seats
                FROM seat_holds sh
                WHERE sh.expires_at > NOW()
                GROUP BY sh.show_id
            ) held_data ON s.show_id = held_data.show_id
            ORDER BY s.show_time ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getBookedSeatsForShow(int showId) {
        String sql = """
            SELECT 
                bs.booking_seat_id,
                bs.booking_id,
                bs.show_id,
                bs.seat_id,
                s.row_char,
                s.seat_num,
                CONCAT(s.row_char, s.seat_num) AS seat_code,
                bs.seat_tier,
                bs.unit_price,
                b.booking_time,
                b.status AS booking_status,
                u.user_id,
                u.full_name AS customer_name,
                u.email AS customer_email,
                u.phone AS customer_phone
            FROM booking_seats bs
            JOIN bookings b ON bs.booking_id = b.booking_id
            JOIN seats s ON bs.seat_id = s.seat_id
            JOIN users u ON b.user_id = u.user_id
            WHERE bs.show_id = ? AND b.status = 'CONFIRMED'
            ORDER BY s.row_char ASC, s.seat_num ASC
        """;
        return jdbcTemplate.queryForList(sql, showId);
    }
}
