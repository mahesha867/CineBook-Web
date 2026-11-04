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
-- Seed Screens (3 Auditoriums)
-- ---------------------------------------------------------------------
INSERT INTO screens (screen_id, screen_name, total_rows, seats_per_row) VALUES
(1, 'Screen 1 - IMAX Laser', 5, 10),
(2, 'Screen 2 - Dolby Atmos', 5, 10),
(3, 'Screen 3 - 4DX Premiere', 5, 10)
ON DUPLICATE KEY UPDATE 
    screen_name = VALUES(screen_name),
    total_rows = VALUES(total_rows),
    seats_per_row = VALUES(seats_per_row);

-- ---------------------------------------------------------------------
-- Seed Seats for Screen 1, Screen 2 & Screen 3
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
(2, 'E', 6, 'VIP'),     (2, 'E', 7, 'VIP'),     (2, 'E', 8, 'VIP'),     (2, 'E', 9, 'VIP'),     (2, 'E', 10, 'VIP'),
-- Screen 3
(3, 'A', 1, 'REGULAR'), (3, 'A', 2, 'REGULAR'), (3, 'A', 3, 'REGULAR'), (3, 'A', 4, 'REGULAR'), (3, 'A', 5, 'REGULAR'),
(3, 'A', 6, 'REGULAR'), (3, 'A', 7, 'REGULAR'), (3, 'A', 8, 'REGULAR'), (3, 'A', 9, 'REGULAR'), (3, 'A', 10, 'REGULAR'),
(3, 'B', 1, 'REGULAR'), (3, 'B', 2, 'REGULAR'), (3, 'B', 3, 'REGULAR'), (3, 'B', 4, 'REGULAR'), (3, 'B', 5, 'REGULAR'),
(3, 'B', 6, 'REGULAR'), (3, 'B', 7, 'REGULAR'), (3, 'B', 8, 'REGULAR'), (3, 'B', 9, 'REGULAR'), (3, 'B', 10, 'REGULAR'),
(3, 'C', 1, 'PREMIUM'), (3, 'C', 2, 'PREMIUM'), (3, 'C', 3, 'PREMIUM'), (3, 'C', 4, 'PREMIUM'), (3, 'C', 5, 'PREMIUM'),
(3, 'C', 6, 'PREMIUM'), (3, 'C', 7, 'PREMIUM'), (3, 'C', 8, 'PREMIUM'), (3, 'C', 9, 'PREMIUM'), (3, 'C', 10, 'PREMIUM'),
(3, 'D', 1, 'PREMIUM'), (3, 'D', 2, 'PREMIUM'), (3, 'D', 3, 'PREMIUM'), (3, 'D', 4, 'PREMIUM'), (3, 'D', 5, 'PREMIUM'),
(3, 'D', 6, 'PREMIUM'), (3, 'D', 7, 'PREMIUM'), (3, 'D', 8, 'PREMIUM'), (3, 'D', 9, 'PREMIUM'), (3, 'D', 10, 'PREMIUM'),
(3, 'E', 1, 'VIP'),     (3, 'E', 2, 'VIP'),     (3, 'E', 3, 'VIP'),     (3, 'E', 4, 'VIP'),     (3, 'E', 5, 'VIP'),
(3, 'E', 6, 'VIP'),     (3, 'E', 7, 'VIP'),     (3, 'E', 8, 'VIP'),     (3, 'E', 9, 'VIP'),     (3, 'E', 10, 'VIP');

-- ---------------------------------------------------------------------
-- Seed Movies (Hi Nanna, Dear Comrade, Avengers Endgame, Spider-Man Brand New Day, Jananayagan, Sita Ramam)
-- ---------------------------------------------------------------------
INSERT INTO movies (movie_id, title, genre, duration_mins, rating, poster_url, description) VALUES
(1, 'Hi Nanna', 'Romance / Drama', 155, 'U/A', 'https://image.tmdb.org/t/p/w500/2vxKa3gMHc84O1iHRkslqeHSsxE.jpg', 'A loving single father and his 6-year-old daughter find their lives transformed when a charming woman enters their world, unraveling a past filled with profound love and heartbreak.'),
(2, 'Dear Comrade', 'Action / Romance', 169, 'U/A', 'https://image.tmdb.org/t/p/w500/sSoMpYjqECgC74a3HAwdBVxFs6H.jpg', 'Bobby, a student union leader with anger management issues, falls deeply in love with Lilly, a passionate state-level cricketer, facing an emotional test of love and sacrifice.'),
(3, 'Avengers: Endgame', 'Action / Sci-Fi', 181, 'PG-13', 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 'After the devastating universe-halving snap by Thanos, the surviving Avengers assemble one final time for a daring quantum time heist to bring everyone back.'),
(4, 'Spider-Man: Brand New Day', 'Action / Adventure', 145, 'PG-13', 'https://upload.wikimedia.org/wikipedia/en/9/9a/Spider-Man_Brand_New_Day_poster.jpg', 'Peter Parker steps into an exhilarating new chapter of his double life, defending the streets of New York City against ruthless emerging villains.'),
(5, 'Jananayagan', 'Action / Political Thriller', 162, 'U/A', 'https://upload.wikimedia.org/wikipedia/en/c/c8/Jana_Nayagan.jpg', 'A charismatic and fearless mass leader takes on deep-rooted corruption, mobilizing millions of ordinary citizens in a high-stakes battle for truth and justice.'),
(6, 'Sita Ramam', 'Romance / Drama', 163, 'U', 'https://image.tmdb.org/t/p/w500/t1O94ZBzsQXJihtVkrsStRLyUDR.jpg', 'An orphaned soldier serving in 1965 Kashmir receives a heartfelt letter from an unknown girl named Sita Mahalakshmi, igniting an unforgettable love across borders.')
ON DUPLICATE KEY UPDATE 
    title = VALUES(title), 
    genre = VALUES(genre), 
    duration_mins = VALUES(duration_mins),
    rating = VALUES(rating),
    poster_url = VALUES(poster_url),
    description = VALUES(description);

-- ---------------------------------------------------------------------
-- Seed Shows (2-3 screens available for every movie)
-- ---------------------------------------------------------------------
INSERT INTO shows (show_id, movie_id, screen_id, show_time, base_price_multiplier) VALUES
-- Movie 1: Hi Nanna (Screens 1, 2, 3)
(1,  1, 1, DATE_ADD(NOW(), INTERVAL 2 HOUR), 1.00),
(2,  1, 2, DATE_ADD(NOW(), INTERVAL 6 HOUR), 1.15),
(3,  1, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 14 HOUR), 1.00),
(19, 1, 3, DATE_ADD(NOW(), INTERVAL 9 HOUR), 0.90),

-- Movie 2: Dear Comrade (Screens 1, 2, 3)
(4,  2, 1, DATE_ADD(NOW(), INTERVAL 3 HOUR), 1.00),
(5,  2, 2, DATE_ADD(NOW(), INTERVAL 7 HOUR), 1.15),
(6,  2, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 15 HOUR), 1.00),
(20, 2, 1, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 19 HOUR), 1.15),

-- Movie 3: Avengers: Endgame (Screens 1, 2, 3)
(7,  3, 1, DATE_ADD(NOW(), INTERVAL 4 HOUR), 1.15),
(8,  3, 2, DATE_ADD(NOW(), INTERVAL 8 HOUR), 1.00),
(9,  3, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), 1.15),
(21, 3, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 21 HOUR), 1.15),

-- Movie 4: Spider-Man: Brand New Day (Screens 1, 2, 3)
(10, 4, 1, DATE_ADD(NOW(), INTERVAL 2 HOUR + INTERVAL 30 MINUTE), 1.00),
(11, 4, 2, DATE_ADD(NOW(), INTERVAL 5 HOUR + INTERVAL 30 MINUTE), 1.00),
(12, 4, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 16 HOUR), 1.00),
(22, 4, 3, DATE_ADD(NOW(), INTERVAL 7 HOUR + INTERVAL 30 MINUTE), 1.15),

-- Movie 5: Jananayagan (Screens 1, 2, 3)
(13, 5, 1, DATE_ADD(NOW(), INTERVAL 3 HOUR + INTERVAL 30 MINUTE), 1.00),
(14, 5, 2, DATE_ADD(NOW(), INTERVAL 7 HOUR + INTERVAL 30 MINUTE), 1.15),
(15, 5, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 20 HOUR), 0.90),
(23, 5, 1, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 17 HOUR), 1.00),

-- Movie 6: Sita Ramam (Screens 1, 2, 3)
(16, 6, 1, DATE_ADD(NOW(), INTERVAL 4 HOUR + INTERVAL 30 MINUTE), 1.00),
(17, 6, 2, DATE_ADD(NOW(), INTERVAL 8 HOUR + INTERVAL 30 MINUTE), 1.00),
(18, 6, 3, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 11 HOUR), 1.00),
(24, 6, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR), 1.00)
ON DUPLICATE KEY UPDATE 
    movie_id = VALUES(movie_id),
    screen_id = VALUES(screen_id),
    show_time = VALUES(show_time),
    base_price_multiplier = VALUES(base_price_multiplier);

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
