# CineBook Web — Academic Viva Questions & Answers Guide

> **Prepared for B.E. Computer Science & Engineering (2nd Year)**
> **Subjects: Object-Oriented Programming using Java (OOP) & Database Management Systems (DBMS)**

---

# PART A: Object-Oriented Programming (OOP using Java)

### Q1. Where and how is **Abstraction** implemented in CineBook Web?
**Answer:**
Abstraction hides internal complexity and exposes only essential contracts. In CineBook Web:
1. **`Bookable` Interface**: Defines the contract (`getId()`, `getName()`, `getPrice()`) implemented by both `Seat` and `Snack`. The checkout and pricing systems treat any inventory item polymorphically without knowing physical layout or food packaging details.
2. **`PricingRule` Interface**: Defines the contract for dynamic surcharges/discounts (`isApplicable(context)`, `calculateAdjustment(context, baseAmount)`).
3. **`SeatRecommendationStrategy` Interface**: Decouples the recommendation algorithm from the rest of the application.
4. **DAO Layer (`MovieDao`, `BookingDao`, `SeatDao`)**: Completely abstracts database queries and connection mechanics away from business services.

---

### Q2. How is **Encapsulation** and **Immutability** demonstrated?
**Answer:**
- **Encapsulation**: Domain models (`Booking`, `Seat`, `Show`) declare private fields and expose controlled getters and setters with validation. For example, `Seat.setRowChar()` sanitizes input and automatically synchronizes the corresponding `SeatType`.
- **Immutability (Value Objects)**: `PriceBreakdown` and `SeatCoordinate` are declared `final` with all `private final` fields. `PriceBreakdown.getRuleAdjustments()` returns an unmodifiable defensive copy (`Collections.unmodifiableMap()`), guaranteeing that financial calculation breakdowns cannot be mutated after evaluation.

---

### Q3. Explain the use of **Inheritance** and **Polymorphism**.
**Answer:**
- **Inheritance**: `AbstractPricingRule` defines common percentage computation logic (`calculateAdjustment`) and is extended by concrete subclasses (`WeekendPricingRule`, `LateNightPricingRule`, `HighOccupancyPricingRule`).
- **Polymorphism**: The `PricingEngine` iterates over a `List<PricingRule>` injected at runtime. When it calls `rule.calculateAdjustment(...)`, dynamic method dispatch invokes the specialized behavior of whichever concrete pricing rule is active, adhering to the Open/Closed Principle (OCP).

---

### Q4. Why is `SeatType` implemented as a Java `enum` rather than database table columns?
**Answer:**
According to the business requirements, seat pricing belongs strictly to application business logic, not database columns. The `SeatType` enum (`REGULAR(150)`, `PREMIUM(250)`, `VIP(400)`) encapsulates tier names, descriptions, authoritative prices, and a factory method `forRow(char rowChar)`. If pricing tiers change, business logic is versioned in Java code without needing destructive schema alterations on millions of inventory seat records.

---

### Q5. What design patterns are used in CineBook Web?
**Answer:**
1. **DAO (Data Access Object) Pattern**: Separates business logic from relational SQL operations.
2. **Strategy Pattern**: Used in `SeatRecommendationStrategy` (`CenterContiguousStrategy` vs `TierOptimizedContiguousStrategy`) and dynamic `PricingRule` evaluations.
3. **Decorator / Chain of Responsibility Pattern**: `PricingEngine` chains base calculations through sequential dynamic pricing rules.
4. **Factory Pattern**: `BookingDao.getNextBookingSequence()` provides thread-safe, collision-free formatted IDs (`CB1001`, `CB1002`).
5. **Singleton & Dependency Injection**: Spring Boot IoC container manages stateless singletons for services and controllers.

---

### Q6. How are Custom Exceptions structured and handled?
**Answer:**
CineBook Web defines domain-specific unchecked exceptions:
- `SeatUnavailableException` (Seat already confirmed booked)
- `SeatHeldException` (Seat currently held under 5-minute countdown)
- `InvalidCouponException` (Coupon expired, invalid, or inactive)
- `DuplicateCancellationException` (Rejects cancelling an already cancelled ticket)
- `BookingNotFoundException` (Invalid booking reference lookup)

A centralized `@RestControllerAdvice` (`GlobalExceptionHandler`) intercepts these exceptions and returns clean, uniform HTTP status codes (HTTP 409 Conflict, HTTP 423 Locked, HTTP 400 Bad Request, HTTP 404 Not Found) with structured JSON payloads.

---

# PART B: Database Management Systems (DBMS using MySQL 8)

### Q7. How is the database normalized to Third Normal Form (3NF)?
**Answer:**
- **1NF**: Every column contains atomic values (no comma-separated lists); composite entities like booking seats and snacks are separated into dedicated tables (`booking_seats`, `booking_snacks`).
- **2NF**: All non-key attributes are fully dependent on the primary key. Composite relationships (`booking_seats`) use surrogate PKs and unique constraints.
- **3NF**: There are no transitive dependencies ($X \to Y \to Z$). Screen attributes (`total_rows`, `seats_per_row`) exist strictly in `screens`. Movie metadata exists strictly in `movies`.
- **Historical Immutability**: `booking_snacks.historical_unit_price` stores the price at transaction time, completely decoupled from future price alterations in `snacks.price`.

---

### Q8. What Transaction Isolation Level is used, and why?
**Answer:**
The system uses **`REPEATABLE READ`** (the default InnoDB isolation level).
- Under `REPEATABLE READ`, MVCC (Multi-Version Concurrency Control) ensures that reads within a transaction see a consistent snapshot of data.
- However, for critical writes, CineBook Web uses **Locking Reads** (`SELECT ... FOR UPDATE`). In MySQL InnoDB, a locking read bypasses the MVCC snapshot and executes a **Current Read**, acquiring exclusive locks on target index records.

---

### Q9. How does CineBook Web prevent **Double-Booking** under high concurrent traffic?
**Answer:**
Double-booking is prevented using a **two-tier defense**:
1. **Application-Level Row Locking**: In `BookingService`, before checking availability or inserting, `bookingDao.lockSeatsForUpdate(showId, seatIds)` issues:
   ```sql
   SELECT seat_id FROM seats WHERE seat_id IN (...) ORDER BY seat_id ASC FOR UPDATE;
   ```
   This acquires exclusive index record locks.
2. **Database-Level Relational Constraint**:
   The table `booking_seats` defines:
   ```sql
   CONSTRAINT uq_show_seat UNIQUE (show_id, seat_id);
   ```
   Even if two threads attempted simultaneous insertion, MySQL's InnoDB B-tree index physically rejects the second insertion with a duplicate key violation, guaranteeing absolute mathematical impossibility of double-booking.

---

### Q10. How does the system prevent **Deadlocks** when multiple threads book overlapping seats?
**Answer:**
Deadlocks occur when Thread 1 holds lock on Seat A and waits for Seat B, while Thread 2 holds lock on Seat B and waits for Seat A (circular wait condition, Coffman condition #4).
CineBook Web **deterministically sorts all seat IDs ascending** before executing `SELECT ... FOR UPDATE`:
```java
List<Integer> sortedSeatIds = new ArrayList<>(seatIds);
Collections.sort(sortedSeatIds);
```
Since every transaction acquires locks in the exact same global order ($A \to B$), circular wait conditions are impossible, completely eliminating deadlocks.

---

### Q11. Explain the Stored Procedures implemented in the project.
**Answer:**
1. **`sp_release_expired_holds(OUT p_released_count INT)`**:
   Deletes rows from `seat_holds` where `expires_at < NOW()` and returns the number of deleted records. Invoked by both the Spring `@Scheduled` background worker and the MySQL scheduled event.
2. **`sp_cancel_booking(IN p_booking_id, OUT p_refund_amount, OUT p_result_code)`**:
   - Executes with row-level locks on `bookings`.
   - Rejects duplicate cancellations.
   - Calculates 80% refund: `p_refund_amount = ROUND(final_total * 0.80, 2)`.
   - Updates `bookings.status = 'CANCELLED'`.
   - Deletes from `booking_seats` (freeing seats for others).
   - Promotes the first waitlisted customer (`status = 'PROMOTED'`).

---

### Q12. Explain the SQL Views and Window Functions used for analytics.
**Answer:**
1. **`v_show_occupancy`**: Aggregates screen capacity, confirmed bookings, active 5-minute holds, and real-time occupancy percentage.
2. **`v_movie_revenue_analytics`**: Aggregates ticket revenue, snack revenue, cancellations, and net revenue partitioned per movie.
3. **`v_top_selling_snacks`**: Demonstrates the SQL Window Function **`DENSE_RANK()`**:
   ```sql
   DENSE_RANK() OVER (ORDER BY COALESCE(SUM(bs.subtotal), 0.00) DESC) AS revenue_rank
   ```
   Ranks concessions by gross revenue generated across all historical orders without leaving gaps in ranking.

---

### Q13. How do Database Triggers provide an automated **Audit Trail**?
**Answer:**
Two MySQL triggers write into the `audit_log` table:
1. **`trg_after_booking_insert`**: Fires on `AFTER INSERT ON bookings`, capturing a JSON snapshot (`JSON_OBJECT`) of booking ID, customer ID, ticket subtotal, discount, and final amount.
2. **`trg_after_booking_cancel`**: Fires on `AFTER UPDATE ON bookings`, logging when `OLD.status = 'CONFIRMED' AND NEW.status = 'CANCELLED'`, preserving the refund amount and cancellation timestamp.
Because these triggers run at the database engine level, an audit trail is recorded even if operations occur through external SQL tools.

