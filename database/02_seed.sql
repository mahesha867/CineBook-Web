-- =====================================================================
-- CineBook Web - Seed Data
-- Database: cinebook_web
-- =====================================================================

USE cinebook_web;

-- ---------------------------------------------------------------------
-- Initialize Booking Sequence (CB1001, CB1002...)
-- ---------------------------------------------------------------------
INSERT INTO booking_seq (id, next_val)
VALUES (1, 1000)
ON DUPLICATE KEY UPDATE next_val = next_val;

-- ---------------------------------------------------------------------
-- Seed Screens
-- ---------------------------------------------------------------------
INSERT INTO screens (screen_id, screen_name, total_rows, seats_per_row) VALUES
(1, 'Screen 1 - IMAX Laser', 5, 10),
(2, 'Screen 2 - Dolby Atmos', 5, 10)
ON DUPLICATE KEY UPDATE screen_name = VALUES(screen_name);

-- ---------------------------------------------------------------------
-- Seed Seats for Screen 1 & Screen 2
-- Rows A-B: REGULAR (Rs. 150)
-- Rows C-D: PREMIUM (Rs. 250)
-- Row E: VIP (Rs. 400)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO seats (screen_id, row_char, seat_num, tier) VALUES
-- Screen 1
(1, 'A', 1, 'REGULAR'), (1, 'A', 2, 'REGULAR'), (1, 'A', 3, 'REGULAR'), (1, 'A', 4, 'REGULAR'), (1, 'A', 5, 'REGULAR'),
(1, 'A', 6, 'REGULAR'), (1, 'A', 7, 'REGULAR'), (1, 'A', 8, 'REGULAR'), (1, 'A', 9, 'REGULAR'), (1, 'A', 10, 'REGULAR'),
(1, 'B', 1, 'REGULAR'), (1, 'B', 2, 'REGULAR'), (1, 'B', 3, 'REGULAR'), (1, 'B', 4, 'REGULAR'), (1, 'B', 5, 'REGULAR'),
(1, 'B', 6, 'REGULAR'), (1, 'B', 7, 'REGULAR'), (1, 'B', 8, 'REGULAR'), (1, 'B', 9, 'REGULAR'), (1, 'B', 10, 'REGULAR'),
(1, 'C', 1, 'PREMIUM'), (1, 'C', 2, 'PREMIUM'), (1, 'C', 3, 'PREMIUM'), (1, 'C', 4, 'PREMIUM'), (1, 'C', 5, 'PREMIUM'),
(1, 'C', 6, 'PREMIUM'), (1, 'C', 7, 'PREMIUM'), (1, 'C', 8, 'PREMIUM'), (1, 'C', 9, 'PREMIUM'), (1, 'C', 10, 'PREMIUM'),
(1, 'D', 1, 'PREMIUM'), (1, 'D', 2, 'PREMIUM'), (1, 'D', 3, 'PREMIUM'), (1, 'D', 4, 'PREMIUM'), (1, 'D', 5, 'PREMIUM'),
(1, 'D', 6, 'PREMIUM'), (1, 'D', 7, 'PREMIUM'), (1, 'D', 8, 'PREMIUM'), (1, 'D', 9, 'PREMIUM'), (1, 'D', 10, 'PREMIUM'),
(1, 'E', 1, 'VIP'),     (1, 'E', 2, 'VIP'),     (1, 'E', 3, 'VIP'),     (1, 'E', 4, 'VIP'),     (1, 'E', 5, 'VIP'),
(1, 'E', 6, 'VIP'),     (1, 'E', 7, 'VIP'),     (1, 'E', 8, 'VIP'),     (1, 'E', 9, 'VIP'),     (1, 'E', 10, 'VIP'),
-- Screen 2
(2, 'A', 1, 'REGULAR'), (2, 'A', 2, 'REGULAR'), (2, 'A', 3, 'REGULAR'), (2, 'A', 4, 'REGULAR'), (2, 'A', 5, 'REGULAR'),
(2, 'A', 6, 'REGULAR'), (2, 'A', 7, 'REGULAR'), (2, 'A', 8, 'REGULAR'), (2, 'A', 9, 'REGULAR'), (2, 'A', 10, 'REGULAR'),
(2, 'B', 1, 'REGULAR'), (2, 'B', 2, 'REGULAR'), (2, 'B', 3, 'REGULAR'), (2, 'B', 4, 'REGULAR'), (2, 'B', 5, 'REGULAR'),
(2, 'B', 6, 'REGULAR'), (2, 'B', 7, 'REGULAR'), (2, 'B', 8, 'REGULAR'), (2, 'B', 9, 'REGULAR'), (2, 'B', 10, 'REGULAR'),
(2, 'C', 1, 'PREMIUM'), (2, 'C', 2, 'PREMIUM'), (2, 'C', 3, 'PREMIUM'), (2, 'C', 4, 'PREMIUM'), (2, 'C', 5, 'PREMIUM'),
(2, 'C', 6, 'PREMIUM'), (2, 'C', 7, 'PREMIUM'), (2, 'C', 8, 'PREMIUM'), (2, 'C', 9, 'PREMIUM'), (2, 'C', 10, 'PREMIUM'),
(2, 'D', 1, 'PREMIUM'), (2, 'D', 2, 'PREMIUM'), (2, 'D', 3, 'PREMIUM'), (2, 'D', 4, 'PREMIUM'), (2, 'D', 5, 'PREMIUM'),
(2, 'D', 6, 'PREMIUM'), (2, 'D', 7, 'PREMIUM'), (2, 'D', 8, 'PREMIUM'), (2, 'D', 9, 'PREMIUM'), (2, 'D', 10, 'PREMIUM'),
(2, 'E', 1, 'VIP'),     (2, 'E', 2, 'VIP'),     (2, 'E', 3, 'VIP'),     (2, 'E', 4, 'VIP'),     (2, 'E', 5, 'VIP'),
(2, 'E', 6, 'VIP'),     (2, 'E', 7, 'VIP'),     (2, 'E', 8, 'VIP'),     (2, 'E', 9, 'VIP'),     (2, 'E', 10, 'VIP');

-- ---------------------------------------------------------------------
-- Seed Movies
-- ---------------------------------------------------------------------
INSERT INTO movies (movie_id, title, genre, duration_mins, rating, poster_url, description) VALUES
(1, 'Interstellar: Beyond Time', 'Sci-Fi / Adventure', 169, 'PG-13', '/images/interstellar.jpg', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.'),
(2, 'The Dark Knight: Legacy', 'Action / Thriller', 152, 'PG-13', '/images/dark-knight.jpg', 'When the menace known as the Joker wreaks havoc and chaos, Batman must accept one of the greatest psychological and physical tests.'),
(3, 'Cyber Odyssey 2099', 'Sci-Fi / Cyberpunk', 138, 'R', '/images/cyberpunk.jpg', 'In a neon-drenched dystopian metropolis, a rogue hacker uncovers a quantum conspiracy that threatens free will.'),
(4, 'Oppenheimer', 'Biography / Drama', 180, 'R', '/images/oppenheimer.jpg', 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.')
ON DUPLICATE KEY UPDATE title = VALUES(title), genre = VALUES(genre), duration_mins = VALUES(duration_mins);

-- ---------------------------------------------------------------------
-- Seed Shows (Dynamic showtimes spanning current and upcoming days)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO shows (show_id, movie_id, screen_id, show_time, base_price_multiplier) VALUES
-- Today / Upcoming Shows
(1, 1, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), 1.00), -- 10:00 AM Morning Show
(2, 1, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), 1.00), -- 02:00 PM Matinee Show
(3, 1, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 18 HOUR), 1.15), -- 06:00 PM Prime Evening Show
(4, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 11 HOUR), 1.00), -- 11:00 AM Show
(5, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 15 HOUR), 1.00), -- 03:00 PM Show
(6, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 19 HOUR), 1.15), -- 07:00 PM Show
(7, 3, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 23 HOUR), 0.90), -- 11:00 PM Late Night Show
(8, 4, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 22 HOUR), INTERVAL 45 MINUTE), 0.90), -- 10:45 PM Late Night Show
-- Tomorrow
(9, 1, 1, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), 1.15),
(10, 3, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 20 HOUR), 1.15);

-- ---------------------------------------------------------------------
-- Seed Snacks (Menu strictly matching academic business rules)
-- ---------------------------------------------------------------------
INSERT INTO snacks (snack_id, name, price, category, is_available) VALUES
('S001', 'Popcorn (Regular Salted)', 120.00, 'FOOD', TRUE),
('S002', 'Cheese Popcorn (Gourmet)', 180.00, 'FOOD', TRUE),
('S003', 'Crispy Nachos with Warm Cheese', 150.00, 'FOOD', TRUE),
('S004', 'French Fries (Peri Peri)', 100.00, 'FOOD', TRUE),
('S005', 'Fresh Veg Club Sandwich', 130.00, 'FOOD', TRUE),
('S006', 'Grilled Chicken Sandwich', 170.00, 'FOOD', TRUE),
('S007', 'Coca-Cola (500ml Fountain)', 80.00, 'BEVERAGE', TRUE),
('S008', 'Sprite (500ml Fountain)', 80.00, 'BEVERAGE', TRUE),
('S009', 'Mineral Water (Packaged 1L)', 40.00, 'BEVERAGE', TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name), price = VALUES(price), category = VALUES(category);

-- ---------------------------------------------------------------------
-- Seed Users (Admin and sample customers)
-- ---------------------------------------------------------------------
INSERT INTO users (user_id, full_name, email, phone, role) VALUES
(1, 'CineBook Admin', 'admin@cinebook.com', '9999999999', 'ADMIN'),
(2, 'Aditya Sharma', 'aditya.sharma@example.com', '9876543210', 'CUSTOMER'),
(3, 'Sneha Patel', 'sneha.patel@example.com', '9876543211', 'CUSTOMER'),
(4, 'Rahul Verma', 'rahul.verma@example.com', '9876543212', 'CUSTOMER')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), phone = VALUES(phone);

-- ---------------------------------------------------------------------
-- Seed Dynamic Pricing Rules
-- ---------------------------------------------------------------------
INSERT INTO pricing_rules (rule_id, rule_name, rule_type, adjustment_percent, condition_threshold, is_active) VALUES
(1, 'Weekend Peak Surcharge', 'WEEKEND_SURCHARGE', 15.00, NULL, TRUE),
(2, 'Late Night Cine Discount', 'LATE_NIGHT_DISCOUNT', -10.00, NULL, TRUE),
(3, 'High Demand Surge (Occupancy > 80%)', 'HIGH_OCCUPANCY_SURCHARGE', 20.00, 80.00, TRUE)
ON DUPLICATE KEY UPDATE rule_name = VALUES(rule_name), adjustment_percent = VALUES(adjustment_percent);

-- ---------------------------------------------------------------------
-- Seed Coupons
-- ---------------------------------------------------------------------
INSERT INTO coupons (coupon_code, discount_percent, valid_from, valid_until, is_active) VALUES
('CINE10', 10.00, '2024-01-01 00:00:00', '2030-12-31 23:59:59', TRUE),
('WELCOME20', 20.00, '2024-01-01 00:00:00', '2030-12-31 23:59:59', TRUE)
ON DUPLICATE KEY UPDATE discount_percent = VALUES(discount_percent), is_active = VALUES(is_active);
