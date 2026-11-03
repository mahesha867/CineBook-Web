-- =====================================================================
-- CineBook Web - Views, Stored Procedures, Triggers & Scheduled Events
-- Database: cinebook_web
-- =====================================================================

USE cinebook_web;

-- ---------------------------------------------------------------------
-- VIEW 1: v_show_occupancy
-- Calculates capacity, confirmed bookings, held seats, and occupancy %
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_show_occupancy AS
SELECT 
    s.show_id,
    m.title AS movie_title,
    sc.screen_name,
    s.show_time,
    (sc.total_rows * sc.seats_per_row) AS total_capacity,
    COALESCE(booked_data.booked_seats, 0) AS booked_seats,
    COALESCE(held_data.held_seats, 0) AS held_seats,
    ROUND(
        (COALESCE(booked_data.booked_seats, 0) / (sc.total_rows * sc.seats_per_row)) * 100, 
        2
    ) AS occupancy_percent,
    COALESCE(waitlist_data.waiting_users, 0) AS waiting_users
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
LEFT JOIN (
    SELECT w.show_id, COUNT(w.waitlist_id) AS waiting_users
    FROM waitlist w
    WHERE w.status = 'WAITING'
    GROUP BY w.show_id
) waitlist_data ON s.show_id = waitlist_data.show_id;

-- ---------------------------------------------------------------------
-- VIEW 2: v_movie_revenue_analytics
-- Aggregates ticket revenue, concession sales, refunds, and net revenue per movie
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_movie_revenue_analytics AS
SELECT 
    m.movie_id,
    m.title AS movie_title,
    m.genre,
    COUNT(DISTINCT b.booking_id) AS total_orders,
    SUM(CASE WHEN b.status = 'CONFIRMED' THEN 1 ELSE 0 END) AS confirmed_orders,
    SUM(CASE WHEN b.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_orders,
    COALESCE(SUM(CASE WHEN b.status = 'CONFIRMED' THEN b.ticket_subtotal - b.ticket_discount ELSE 0 END), 0.00) AS gross_ticket_revenue,
    COALESCE(SUM(b.snack_subtotal), 0.00) AS gross_snack_revenue,
    COALESCE(SUM(b.refund_amount), 0.00) AS total_refunds,
    COALESCE(
        SUM(CASE WHEN b.status = 'CONFIRMED' THEN b.final_total ELSE (b.final_total - b.refund_amount) END), 
        0.00
    ) AS net_revenue
FROM movies m
LEFT JOIN shows s ON m.movie_id = s.movie_id
LEFT JOIN bookings b ON s.show_id = b.show_id
GROUP BY m.movie_id, m.title, m.genre;

-- ---------------------------------------------------------------------
-- VIEW 3: v_top_selling_snacks
-- Ranks concession snacks by sales volume and gross revenue using SQL window functions
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW v_top_selling_snacks AS
SELECT 
    sn.snack_id,
    sn.name AS snack_name,
    sn.category,
    sn.price AS current_menu_price,
    COALESCE(SUM(bs.quantity), 0) AS total_quantity_sold,
    COALESCE(SUM(bs.subtotal), 0.00) AS total_revenue_generated,
    DENSE_RANK() OVER (ORDER BY COALESCE(SUM(bs.subtotal), 0.00) DESC) AS revenue_rank
FROM snacks sn
LEFT JOIN booking_snacks bs ON sn.snack_id = bs.snack_id
GROUP BY sn.snack_id, sn.name, sn.category, sn.price;

-- ---------------------------------------------------------------------
-- STORED PROCEDURE 1: sp_release_expired_holds
-- Periodically purges seat holds whose timeout has lapsed
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_release_expired_holds;
DELIMITER //
CREATE PROCEDURE sp_release_expired_holds(OUT p_released_count INT)
BEGIN
    DELETE FROM seat_holds WHERE expires_at < NOW();
    SET p_released_count = ROW_COUNT();
END //
DELIMITER ;

-- ---------------------------------------------------------------------
-- STORED PROCEDURE 2: sp_cancel_booking
-- Transactionally processes 80% refund, frees booked seats, updates status,
-- and promotes the first waitlisted customer for the show
-- ---------------------------------------------------------------------
DROP PROCEDURE IF EXISTS sp_cancel_booking;
DELIMITER //
CREATE PROCEDURE sp_cancel_booking(
    IN p_booking_id VARCHAR(20),
    OUT p_refund_amount DECIMAL(10,2),
    OUT p_result_code VARCHAR(30)
)
proc_label: BEGIN
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_final_total DECIMAL(10,2);
    DECLARE v_show_id INT;
    DECLARE v_waitlist_id INT DEFAULT NULL;

    -- Retrieve booking details with row-level lock
    SELECT status, final_total, show_id 
    INTO v_current_status, v_final_total, v_show_id
    FROM bookings
    WHERE booking_id = p_booking_id
    FOR UPDATE;

    IF v_current_status IS NULL THEN
        SET p_result_code = 'NOT_FOUND';
        SET p_refund_amount = 0.00;
        LEAVE proc_label;
    END IF;

    IF v_current_status = 'CANCELLED' THEN
        SET p_result_code = 'ALREADY_CANCELLED';
        SET p_refund_amount = 0.00;
        LEAVE proc_label;
    END IF;

    -- Calculate 80% refund on total paid
    SET p_refund_amount = ROUND(v_final_total * 0.80, 2);

    -- Update booking status and refund
    UPDATE bookings 
    SET status = 'CANCELLED',
        refund_amount = p_refund_amount,
        cancelled_at = NOW()
    WHERE booking_id = p_booking_id;

    -- Free seats by deleting from booking_seats (snacks remain in booking_snacks for audit)
    DELETE FROM booking_seats WHERE booking_id = p_booking_id;

    -- Waitlist auto-promotion: check if any customer is waiting for this show
    SELECT waitlist_id INTO v_waitlist_id
    FROM waitlist
    WHERE show_id = v_show_id AND status = 'WAITING'
    ORDER BY created_at ASC
    LIMIT 1;

    IF v_waitlist_id IS NOT NULL THEN
        UPDATE waitlist 
        SET status = 'PROMOTED',
            promoted_at = NOW()
        WHERE waitlist_id = v_waitlist_id;
    END IF;

    SET p_result_code = 'SUCCESS';
END //
DELIMITER ;

-- ---------------------------------------------------------------------
-- TRIGGER 1: trg_after_booking_insert
-- Logs every new booking creation into the audit_log table
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_booking_insert;
DELIMITER //
CREATE TRIGGER trg_after_booking_insert
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, action_type, record_id, details)
    VALUES (
        'bookings',
        'BOOKING_CREATED',
        NEW.booking_id,
        JSON_OBJECT(
            'booking_id', NEW.booking_id,
            'user_id', NEW.user_id,
            'show_id', NEW.show_id,
            'ticket_subtotal', NEW.ticket_subtotal,
            'ticket_discount', NEW.ticket_discount,
            'snack_subtotal', NEW.snack_subtotal,
            'final_total', NEW.final_total,
            'coupon_code', NEW.coupon_code,
            'status', NEW.status
        )
    );
END //
DELIMITER ;

-- ---------------------------------------------------------------------
-- TRIGGER 2: trg_after_booking_cancel
-- Logs every booking cancellation and refund amount into the audit_log table
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS trg_after_booking_cancel;
DELIMITER //
CREATE TRIGGER trg_after_booking_cancel
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF OLD.status = 'CONFIRMED' AND NEW.status = 'CANCELLED' THEN
        INSERT INTO audit_log (table_name, action_type, record_id, details)
        VALUES (
            'bookings',
            'BOOKING_CANCELLED',
            NEW.booking_id,
            JSON_OBJECT(
                'booking_id', NEW.booking_id,
                'refund_amount', NEW.refund_amount,
                'cancelled_at', NEW.cancelled_at
            )
        );
    END IF;
END //
DELIMITER ;

-- ---------------------------------------------------------------------
-- SCHEDULED EVENT: evt_clean_expired_seat_holds
-- Automatically invokes sp_release_expired_holds every 1 minute
-- ---------------------------------------------------------------------
SET GLOBAL event_scheduler = ON;

DROP EVENT IF EXISTS evt_clean_expired_seat_holds;
CREATE EVENT evt_clean_expired_seat_holds
ON SCHEDULE EVERY 1 MINUTE
DO
    CALL sp_release_expired_holds(@dummy);

