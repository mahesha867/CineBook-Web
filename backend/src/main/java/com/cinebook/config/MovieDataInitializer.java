package com.cinebook.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Automatically synchronizes screens, seat maps, movie catalog, and dynamic showtimes
 * so that all 6 featured movies continuously have 2-3 active screens available.
 */
@Component
public class MovieDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MovieDataInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MovieDataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Synchronizing CineBook screens, seats, movie catalog, and screenings...");
            initScreensAndSeats();
            initMovies();
            initShows();
            rollPastShowsForward();
            log.info("CineBook screens, seats, and 2-3 bookable screens per movie initialized successfully.");
        } catch (Exception e) {
            log.warn("MovieDataInitializer notice: {}", e.getMessage(), e);
        }
    }

    /**
     * Periodic safety schedule: Runs every 30 minutes.
     * Ensures any shows that expired are rolled forward by whole days so that
     * every movie always maintains active upcoming screenings regardless of server uptime.
     */
    @Scheduled(fixedRate = 1800000)
    public void rollPastShowsForward() {
        try {
            String rollSql = """
                UPDATE shows 
                SET show_time = DATE_ADD(show_time, INTERVAL CEIL(TIMESTAMPDIFF(HOUR, show_time, NOW()) / 24.0) DAY)
                WHERE show_time < NOW() - INTERVAL 1 HOUR;
            """;
            int updated = jdbcTemplate.update(rollSql);
            if (updated > 0) {
                log.info("Rolled forward {} past showtimes to ensure continuous booking availability.", updated);
            }
        } catch (Exception e) {
            log.debug("Notice on rollPastShowsForward: {}", e.getMessage());
        }
    }

    private void initScreensAndSeats() {
        // Ensure Screens 1, 2, and 3 exist
        String screensSql = """
            INSERT INTO screens (screen_id, screen_name, total_rows, seats_per_row) VALUES
            (1, 'Screen 1 - IMAX Laser', 5, 10),
            (2, 'Screen 2 - Dolby Atmos', 5, 10),
            (3, 'Screen 3 - 4DX Premiere', 5, 10)
            ON DUPLICATE KEY UPDATE 
                screen_name = VALUES(screen_name),
                total_rows = VALUES(total_rows),
                seats_per_row = VALUES(seats_per_row);
        """;
        jdbcTemplate.execute(screensSql);

        // Ensure 50 seats exist for each of Screen 1, 2, and 3
        String seatsSql = """
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
        """;
        jdbcTemplate.execute(seatsSql);
    }

    private void initMovies() {
        String movieSql = """
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
        """;
        jdbcTemplate.execute(movieSql);
    }

    private void initShows() {
        String showSql = """
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
        """;
        jdbcTemplate.execute(showSql);
    }
}
