# CineBook Web — Entity-Relationship (ER) Architecture

## 1. Relational Entity-Relationship Diagram (Mermaid)

```mermaid
erDiagram
    MOVIES ||--o{ SHOWS : "schedules"
    SCREENS ||--o{ SEATS : "contains"
    SCREENS ||--o{ SHOWS : "hosts"
    USERS ||--o{ BOOKINGS : "places"
    SHOWS ||--o{ BOOKINGS : "booked_for"
    BOOKINGS ||--|{ BOOKING_SEATS : "reserves"
    SEATS ||--o{ BOOKING_SEATS : "allocated_in"
    SNACKS ||--o{ BOOKING_SNACKS : "ordered_in"
    BOOKINGS ||--o{ BOOKING_SNACKS : "includes"
    SHOWS ||--o{ SEAT_HOLDS : "temporarily_locks"
    SEATS ||--o{ SEAT_HOLDS : "held_under"
    SHOWS ||--o{ WAITLIST : "queues_for"
    USERS ||--o{ WAITLIST : "joins"

    MOVIES {
        int movie_id PK
        varchar title
        varchar genre
        int duration_mins
        varchar rating
        varchar poster_url
        text description
        boolean is_active
        timestamp created_at
    }

    SCREENS {
        int screen_id PK
        varchar screen_name UK
        int total_rows
        int seats_per_row
    }

    SEATS {
        int seat_id PK
        int screen_id FK
        char row_char
        int seat_num
        enum tier
    }

    SHOWS {
        int show_id PK
        int movie_id FK
        int screen_id FK
        datetime show_time
        decimal base_price_multiplier
        boolean is_cancelled
    }

    USERS {
        int user_id PK
        varchar full_name
        varchar email UK
        varchar phone UK
        enum role
        timestamp created_at
    }

    BOOKINGS {
        varchar booking_id PK
        int user_id FK
        int show_id FK
        timestamp booking_time
        decimal ticket_subtotal
        decimal ticket_discount
        decimal snack_subtotal
        decimal final_total
        varchar coupon_code
        enum status
        decimal refund_amount
        datetime cancelled_at
    }

    BOOKING_SEATS {
        int booking_seat_id PK
        varchar booking_id FK
        int show_id FK
        int seat_id FK
        decimal unit_price
        varchar seat_tier
    }

    SNACKS {
        varchar snack_id PK
        varchar name
        decimal price
        enum category
        boolean is_available
    }

    BOOKING_SNACKS {
        int booking_snack_id PK
        varchar booking_id FK
        varchar snack_id FK
        int quantity
        decimal historical_unit_price
        decimal subtotal
    }

    SEAT_HOLDS {
        int hold_id PK
        int show_id FK
        int seat_id FK
        varchar session_id
        timestamp held_at
        timestamp expires_at
    }

    WAITLIST {
        int waitlist_id PK
        int show_id FK
        int user_id FK
        int seats_requested
        enum preferred_tier
        enum status
        timestamp created_at
        datetime promoted_at
    }

    PRICING_RULES {
        int rule_id PK
        varchar rule_name
        enum rule_type
        decimal adjustment_percent
        decimal condition_threshold
        boolean is_active
    }

    COUPONS {
        varchar coupon_code PK
        decimal discount_percent
        datetime valid_from
        datetime valid_until
        boolean is_active
    }

    AUDIT_LOG {
        int log_id PK
        varchar table_name
        varchar action_type
        varchar record_id
        json details
        timestamp created_at
    }

    BOOKING_SEQ {
        int id PK
        bigint next_val
    }
```

---

## 2. Relational Schema Normalization (3NF Justification)

1. **First Normal Form (1NF)**:
   - Every column contains atomic values. All multivalue attributes (such as seats in a booking or concession orders) are decoupled into dedicated child tables (`booking_seats`, `booking_snacks`).
   - Every table possesses an explicit primary key (`PK`).

2. **Second Normal Form (2NF)**:
   - All tables are in 1NF and every non-key attribute is fully functionally dependent on the entire primary key.
   - In composite scenarios (`booking_seats`), foreign key references (`booking_id`, `show_id`, `seat_id`) are uniquely keyed via surrogate `booking_seat_id` and reinforced by composite unique keys (`uq_show_seat`).

3. **Third Normal Form (3NF)**:
   - No transitive dependencies exist.
   - Screen dimensions (`total_rows`, `seats_per_row`) reside strictly in `screens`, not duplicated inside `shows` or `seats`.
   - Movie metadata (`genre`, `duration_mins`) resides strictly in `movies`, not in `shows`.
   - Customer credentials reside strictly in `users`.
   - Historical pricing integrity: `booking_snacks.historical_unit_price` preserves historical transactional snapshot independent of menu mutations in `snacks.price`.

---

## 3. Database Indexes & Justifications

| Index Name | Table & Columns | Academic / Performance Justification |
|---|---|---|
| `idx_shows_movie_time` | `shows(movie_id, show_time)` | Optimizes user navigation on the movie details page, filtering screenings by movie ID in chronological order ($O(\log N)$ range scan). |
| `idx_seat_holds_expiry` | `seat_holds(expires_at)` | Accelerates the high-frequency 15-second Spring cleanup job and MySQL 1-minute event sweep (`WHERE expires_at < NOW()`). |
| `idx_bookings_show_status` | `bookings(show_id, status)` | Crucial for real-time occupancy computations and seat map availability checks (`WHERE show_id = ? AND status = 'CONFIRMED'`). |
| `idx_bookings_user_time` | `bookings(user_id, booking_time DESC)` | Accelerates customer reservation retrieval in the "My Tickets" portal. |
| `uq_show_seat` | `booking_seats(show_id, seat_id)` | Enforces physical impossibility of double-booking the same seat for a single screening at the relational database engine level. |

