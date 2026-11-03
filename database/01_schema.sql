-- =====================================================================
-- CineBook Web - Database Schema (3NF)
-- Database: cinebook_web
-- Strict isolation: Does NOT touch any existing databases
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cinebook_web
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cinebook_web;

-- ---------------------------------------------------------------------
-- 1. Movies Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duration_mins INT NOT NULL CHECK (duration_mins > 0),
    rating VARCHAR(10) NOT NULL,
    poster_url VARCHAR(255),
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. Screens Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS screens (
    screen_id INT AUTO_INCREMENT PRIMARY KEY,
    screen_name VARCHAR(50) NOT NULL UNIQUE,
    total_rows INT NOT NULL CHECK (total_rows > 0),
    seats_per_row INT NOT NULL CHECK (seats_per_row > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. Seats Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    screen_id INT NOT NULL,
    row_char CHAR(1) NOT NULL,
    seat_num INT NOT NULL CHECK (seat_num > 0),
    tier ENUM('REGULAR', 'PREMIUM', 'VIP') NOT NULL,
    CONSTRAINT fk_seats_screen FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE CASCADE,
    CONSTRAINT uq_screen_seat UNIQUE (screen_id, row_char, seat_num)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. Shows Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shows (
    show_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    screen_id INT NOT NULL,
    show_time DATETIME NOT NULL,
    base_price_multiplier DECIMAL(3,2) NOT NULL DEFAULT 1.00 CHECK (base_price_multiplier > 0),
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_shows_movie FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE RESTRICT,
    CONSTRAINT fk_shows_screen FOREIGN KEY (screen_id) REFERENCES screens(screen_id) ON DELETE RESTRICT,
    CONSTRAINT uq_screen_showtime UNIQUE (screen_id, show_time)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. Users Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    role ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. Booking Sequence Table (Collision-Safe ID Generation: CB1001, CB1002...)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS booking_seq (
    id INT PRIMARY KEY,
    next_val BIGINT NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. Bookings Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    show_id INT NOT NULL,
    booking_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ticket_subtotal DECIMAL(10,2) NOT NULL CHECK (ticket_subtotal >= 0),
    ticket_discount DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (ticket_discount >= 0),
    snack_subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (snack_subtotal >= 0),
    final_total DECIMAL(10,2) NOT NULL CHECK (final_total >= 0),
    coupon_code VARCHAR(30) NULL,
    status ENUM('CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (refund_amount >= 0),
    cancelled_at DATETIME NULL,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_show FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. Booking Seats Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS booking_seats (
    booking_seat_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(20) NOT NULL,
    show_id INT NOT NULL,
    seat_id INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    seat_tier VARCHAR(20) NOT NULL,
    CONSTRAINT fk_bseats_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bseats_show FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    CONSTRAINT fk_bseats_seat FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT,
    CONSTRAINT uq_show_seat UNIQUE (show_id, seat_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. Snacks Table (Concessions Menu)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS snacks (
    snack_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category ENUM('FOOD', 'BEVERAGE') NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. Booking Snacks Table (Historical Order Items)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS booking_snacks (
    booking_snack_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(20) NOT NULL,
    snack_id VARCHAR(10) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    historical_unit_price DECIMAL(10,2) NOT NULL CHECK (historical_unit_price >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    CONSTRAINT fk_bsnacks_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    CONSTRAINT fk_bsnacks_snack FOREIGN KEY (snack_id) REFERENCES snacks(snack_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. Seat Holds Table (Temporary 5-minute reservations)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS seat_holds (
    hold_id INT AUTO_INCREMENT PRIMARY KEY,
    show_id INT NOT NULL,
    seat_id INT NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    held_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_holds_show FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    CONSTRAINT fk_holds_seat FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE CASCADE,
    CONSTRAINT uq_show_seat_hold UNIQUE (show_id, seat_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. Waitlist Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS waitlist (
    waitlist_id INT AUTO_INCREMENT PRIMARY KEY,
    show_id INT NOT NULL,
    user_id INT NOT NULL,
    seats_requested INT NOT NULL CHECK (seats_requested > 0),
    preferred_tier ENUM('ANY', 'REGULAR', 'PREMIUM', 'VIP') NOT NULL DEFAULT 'ANY',
    status ENUM('WAITING', 'PROMOTED', 'EXPIRED') NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    promoted_at DATETIME NULL,
    CONSTRAINT fk_waitlist_show FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE,
    CONSTRAINT fk_waitlist_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 13. Dynamic Pricing Rules Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pricing_rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(60) NOT NULL,
    rule_type ENUM('WEEKEND_SURCHARGE', 'LATE_NIGHT_DISCOUNT', 'HIGH_OCCUPANCY_SURCHARGE') NOT NULL,
    adjustment_percent DECIMAL(5,2) NOT NULL,
    condition_threshold DECIMAL(5,2) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 14. Coupons Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS coupons (
    coupon_code VARCHAR(30) PRIMARY KEY,
    discount_percent DECIMAL(5,2) NOT NULL CHECK (discount_percent BETWEEN 0 AND 100),
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 15. Audit Log Table
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    action_type VARCHAR(30) NOT NULL,
    record_id VARCHAR(50) NOT NULL,
    details JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =====================================================================
-- Performance Indexes & Academic Justification
-- =====================================================================

-- Justification 1: Movie showtime lookups
-- Filter by movie_id and chronologically order by show_time for frontend movie detail page
CREATE INDEX idx_shows_movie_time ON shows(movie_id, show_time);

-- Justification 2: Seat hold expiration sweep
-- High-frequency cleanup jobs query `WHERE expires_at < NOW()`; indexing expires_at ensures constant time lookups
CREATE INDEX idx_seat_holds_expiry ON seat_holds(expires_at);

-- Justification 3: Occupancy and seat availability
-- Querying confirmed active bookings per show (`WHERE show_id = ? AND status = 'CONFIRMED'`) powers the real-time seat map and dynamic pricing
CREATE INDEX idx_bookings_show_status ON bookings(show_id, status);

-- Justification 4: Customer booking history lookup
-- Rapid lookup by user ID ordered chronologically descending for "My Tickets"
CREATE INDEX idx_bookings_user_time ON bookings(user_id, booking_time DESC);
