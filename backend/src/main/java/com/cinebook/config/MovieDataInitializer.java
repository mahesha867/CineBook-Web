package com.cinebook.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Automatically synchronizes the movie catalog and dynamic showtimes on application startup.
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
            log.info("Synchronizing movie catalog and showtimes...");

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

            String showSql = """
                INSERT INTO shows (show_id, movie_id, screen_id, show_time, base_price_multiplier) VALUES
                -- Screen 1 (IMAX Laser)
                (1, 1, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 10 HOUR), 1.00),
                (2, 1, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 14 HOUR), 1.00),
                (3, 3, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 18 HOUR), 1.15),
                (4, 5, 1, DATE_ADD(CURRENT_DATE(), INTERVAL 22 HOUR), 0.90),
                -- Screen 2 (Dolby Atmos)
                (5, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 11 HOUR), 1.00),
                (6, 6, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 15 HOUR), 1.00),
                (7, 4, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 19 HOUR), 1.15),
                (8, 2, 2, DATE_ADD(CURRENT_DATE(), INTERVAL 23 HOUR), 0.90),
                -- Tomorrow Screenings
                (9, 6, 1, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 14 HOUR), 1.00),
                (10, 3, 1, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), 1.15),
                (11, 5, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 16 HOUR), 1.00),
                (12, 4, 2, DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), INTERVAL 20 HOUR), 1.15)
                ON DUPLICATE KEY UPDATE 
                    movie_id = VALUES(movie_id),
                    screen_id = VALUES(screen_id),
                    show_time = VALUES(show_time),
                    base_price_multiplier = VALUES(base_price_multiplier);
            """;
            jdbcTemplate.execute(showSql);
            log.info("Movie catalog and dynamic screenings updated successfully.");
        } catch (Exception e) {
            log.warn("MovieDataInitializer notice: {}", e.getMessage());
        }
    }
}

