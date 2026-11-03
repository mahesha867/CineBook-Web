# 🎬 CineBook Web

> **A Production-Quality Cinema Reservation & Concessions Platform**  
> *Developed for 2nd-Year B.E. Computer Science & Engineering (OOP using Java + Database Management Systems)*

[![Live Demo](https://img.shields.io/badge/Live%20Demo-cinebook--web--qo78.onrender.com-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://cinebook-web-qo78.onrender.com)

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL 8](https://img.shields.io/badge/MySQL-8.0%20(InnoDB)-blue.svg)](https://www.mysql.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Tests-12%20Passed%20(100%25)-success.svg)]()

---

## 🌐 Live Production Deployment

CineBook Web is actively deployed in production on **Render** (Docker containerized Spring Boot 3) connected to an **Aiven Cloud MySQL 8.0** cluster:

- 🎟️ **Customer Reservation Portal**: [https://cinebook-web-qo78.onrender.com](https://cinebook-web-qo78.onrender.com)
- 🎫 **My Tickets & Refund Center**: [https://cinebook-web-qo78.onrender.com/my-tickets.html](https://cinebook-web-qo78.onrender.com/my-tickets.html)
- 📊 **Executive Analytics Dashboard**: [https://cinebook-web-qo78.onrender.com/admin.html](https://cinebook-web-qo78.onrender.com/admin.html) *(Credentials: `admin` / `admin123`)*

---

## 📌 Executive Summary

**CineBook Web** is an end-to-end cinema ticketing web platform engineered to demonstrate high-level **Object-Oriented Programming (OOP)** patterns in Java 21 coupled with rigorous **Relational Database Management System (DBMS)** architecture in MySQL 8.

Unlike basic CRUD applications, CineBook Web implements plain JDBC with Spring's `JdbcTemplate` (explicitly zero ORM/Hibernate), manual transaction boundaries (`REPEATABLE READ`), deterministic row-level locking (`SELECT ... FOR UPDATE`), MySQL stored procedures, analytical SQL views with window functions (`DENSE_RANK`), audit triggers, automated scheduled events, and cryptographic ZXing QR code digital boarding passes.

---

## 🛠 Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.4 (`Web`, `Validation`, `JDBC`, `Scheduling`), Google ZXing 3.5.3 (QR code generation), Maven 3.9.9.
- **Data Access**: Plain JDBC via `JdbcTemplate` and the **DAO Pattern** (no JPA/Hibernate to demonstrate explicit transaction and SQL control).
- **Database**: MySQL 8.0 (InnoDB), database `cinebook_web`.
- **Frontend**: Responsive Dark Cinema Theme built with pure HTML5, CSS3, and modern Vanilla ES6+ JavaScript (served directly by Spring Boot). Chart.js for executive analytics.
- **Testing**: JUnit 5, Mockito, AssertJ, multi-threaded concurrency testing.

---

## 🚀 Key Business Rules & Pricing Logic

1. **Seat Tiers in Java**: Seat pricing lives strictly in Java domain code (`SeatType` enum), not hardcoded in the database:
   - **Rows A-B**: `REGULAR` — Rs. 150.00
   - **Rows C-D**: `PREMIUM` — Rs. 250.00
   - **Row E**: `VIP Recliner` — Rs. 400.00
2. **Dynamic Concessions**: Snacks come dynamically from the database (`S001` - `S009`). Historical unit prices and subtotals are preserved at transaction time; future menu price changes never alter historical audit records.
3. **Coupon Application**: Coupon `CINE10` applies a 10% discount strictly to the **Ticket Subtotal ONLY**. Concession snacks **never** receive ticket discounts.
4. **Final Bill Formula**:
   $$\text{Final Total} = (\text{Ticket Subtotal} - \text{Ticket Discount}) + \text{Snack Subtotal}$$
5. **Cancellations & 80% Refunds**:
   - Frees reserved seats immediately.
   - Preserves snack audit rows.
   - Calculates and refunds exactly **80% of total paid**.
   - Strictly rejects duplicate cancellations (`DuplicateCancellationException`).
6. **Collision-Safe Booking IDs**: Formatted as `CB1001`, `CB1002`... via the `booking_seq` sequence table.
7. **ACID Concurrency**: Deterministic row-level locking (`SELECT ... FOR UPDATE` sorted by `seat_id` ascending) in `REPEATABLE READ` transactions to guarantee zero deadlocks and zero double-bookings.

---

## ✨ Innovative Features

1. **⚡ Smart Seat Hold (5-Minute Lock)**:
   - When a user clicks a seat, an exclusive 5-minute hold is acquired in `seat_holds`.
   - Other users immediately see held seats in yellow/amber (`HELD`).
   - A live countdown timer bar is rendered in the UI (`04:59` countdown).
   - A Spring `@Scheduled` background task and a MySQL `EVENT` automatically invoke `sp_release_expired_holds()`.
2. **🎯 Best-Seat Recommender (Strategy Pattern)**:
   - User requests $N$ adjacent seats.
   - **Strategy 1**: `CenterContiguousStrategy` calculates distance from screen center sweet-spot ($|row - idealRow| \times 2 + |col - centerCol|$).
   - **Strategy 2**: `TierOptimizedContiguousStrategy` strictly filters by desired tier (VIP/Premium) before centering.
3. **👥 Automated Waitlist with Auto-Promotion**:
   - When a screening is sold out, users join a FIFO waitlist queue.
   - When any booking is cancelled, the transactional cancellation service immediately promotes the first waiting user.
4. **📈 Dynamic Pricing Engine**:
   - Dynamic rules evaluated via Strategy / Chain of Responsibility:
     - Weekend surcharge (+15% for Friday evening, Saturday, Sunday).
     - Late-night discount (-10% for shows at or after 22:30).
     - High-occupancy surge (+20% when show exceeds 80% occupancy).
   - Itemized transparent price breakdown presented to the user.
5. **🎟 Digital Ticket with ZXing QR Code**:
   - Generates a Base64 PNG QR code embedded directly into a modern boarding-pass card.
   - Lookup past tickets in "My Tickets" by Booking ID, Phone, or Email.
6. **📊 Admin Analytics Dashboard (`/admin.html`)**:
   - Secure sign-in (`admin` / `admin123`).
   - KPI metric cards: Net Realized Revenue, Occupancy Rate, Total Bookings, Cancellation Rate.
   - Chart.js visual charts:
     - Multi-axis Bar Chart: Revenue by Movie (Tickets vs Snacks).
     - Occupancy per Show (Bar chart).
     - Top Concessions: Ranked using MySQL Window Function `DENSE_RANK()`.
     - Peak Booking Hours (Line chart).
7. **🛡 Database Trigger Audit Trail**:
   - Triggers `trg_after_booking_insert` and `trg_after_booking_cancel` write real-time JSON snapshots into the `audit_log` table.

---

## 🗄 Database Setup & Architecture

### Step 1: Execute SQL Scripts in MySQL 8
The schema scripts are located in `/database`:

```bash
# Connect to MySQL and run in order:
mysql -u root -p < database/01_schema.sql
mysql -u root -p < database/02_seed.sql
mysql -u root -p < database/03_views_procs_triggers.sql
```

### Database Highlights:
- **Database Name**: `cinebook_web` (strictly isolated).
- **Tables (15)**: `movies`, `screens`, `seats`, `shows`, `users`, `booking_seq`, `bookings`, `booking_seats`, `snacks`, `booking_snacks`, `seat_holds`, `waitlist`, `pricing_rules`, `coupons`, `audit_log`.
- **Relational Integrity**: Foreign keys (`ON DELETE CASCADE` / `RESTRICT`), check constraints, and composite unique constraints (`uq_show_seat`).
- **Views (3)**: `v_show_occupancy`, `v_movie_revenue_analytics`, `v_top_selling_snacks`.
- **Stored Procedures (2)**: `sp_release_expired_holds`, `sp_cancel_booking`.
- **Triggers (2)**: `trg_after_booking_insert`, `trg_after_booking_cancel`.
- **Scheduled Event (1)**: `evt_clean_expired_seat_holds` (runs every 1 minute).

Detailed ER Diagram and relational schema available at [docs/DBMS.md](docs/DBMS.md).

---

## 💻 Running the Application

### Prerequisites:
- Java 21 JDK (Adoptium / Temurin / Oracle)
- MySQL 8.0 running on localhost:3306 with database `cinebook_web`

### Build and Run:
```bash
# Navigate to backend directory
cd backend

# Compile and start with Maven wrapper
./mvnw spring-boot:run
```
*(On Windows PowerShell, run `.\mvnw.cmd spring-boot:run`)*

### Access the Application:

#### 🌐 Live Cloud (Render):
- **Customer Reservation Portal**: [https://cinebook-web-qo78.onrender.com](https://cinebook-web-qo78.onrender.com)
- **My Tickets & Refund Center**: [https://cinebook-web-qo78.onrender.com/my-tickets.html](https://cinebook-web-qo78.onrender.com/my-tickets.html)
- **Admin Analytics Dashboard**: [https://cinebook-web-qo78.onrender.com/admin.html](https://cinebook-web-qo78.onrender.com/admin.html) *(Credentials: `admin` / `admin123`)*

#### 💻 Local Development:
- **Customer Portal**: [http://localhost:8080](http://localhost:8080)
- **My Tickets**: [http://localhost:8080/my-tickets.html](http://localhost:8080/my-tickets.html)
- **Admin Portal**: [http://localhost:8080/admin.html](http://localhost:8080/admin.html) *(Credentials: `admin` / `admin123`)*

> 📖 **System Architecture & Deployment Guide**: For details on multi-tier architecture, sequence diagrams, and Docker containerization, see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## 🧪 Comprehensive Test Suite (12 Tests Passing)

Execute the test suite verifying all academic requirements:

```bash
cd backend
./mvnw test
```

### Verified Test Cases:
| Test Class | Test Name | Verification Goal |
|---|---|---|
| `BookingConcurrencyTest` | `testConcurrentBookingSameSeatAllowsExactlyOneWinner` | Two threads simultaneously attempt to book the exact same seat. Proves deterministic row-level locks and `uq_show_seat` constraint allow **exactly 1 winner** and 0 double bookings. |
| `BookingServiceIntegrationTest` | `testCancellationIssues80PercentRefundAndFreesSeats` | Confirms booking, cancels it, asserts **80% refund**, `CANCELLED` status, and freed seats. |
| `BookingServiceIntegrationTest` | `testDuplicateCancellationRejected` | Asserts second cancellation throws `DuplicateCancellationException`. |
| `BookingServiceIntegrationTest` | `testHistoricalSnackPricePreservedAfterMenuUpdate` | Modifies snack price in menu; asserts historical unit price in past order remains unchanged. |
| `BookingServiceIntegrationTest` | `testWaitlistAutoPromotionOnCancellation` | Cancelling a booking auto-promotes the first customer waiting in the queue. |
| `BookingServiceIntegrationTest` | `testSeatHoldExpiryReleasesSeats` | Validates that expired 5-minute holds are automatically purged and seats returned to `AVAILABLE`. |
| `BookingServiceIntegrationTest` | `testAtomicRollbackOnFailure` | Verifies that mid-transaction failures result in complete rollback with zero orphaned records. |
| `PricingEngineTest` | `testCouponAppliesToTicketsOnly` | Asserts `CINE10` discounts ticket subtotal only; snacks receive zero ticket discount. |
| `PricingEngineTest` | `testWeekendSurchargeApplication` | Verifies +15% dynamic surcharge on weekend screenings. |
| `PricingEngineTest` | `testLateNightDiscount` | Verifies -10% dynamic discount for late screenings after 22:30. |
| `SeatRecommendationStrategyTest` | `testCenterContiguousStrategy` | Asserts center-first algorithm selects middle row (Row C) and central seats (5 & 6). |
| `SeatRecommendationStrategyTest` | `testTierOptimizedStrategy` | Asserts tier-optimized algorithm strictly selects VIP recliner seats. |

---

## 📚 Project Presentation & Viva Preparation Guides

Comprehensive documentation modules have been created in `/docs` specifically structured for project presentation and academic viva defense:

1. 🏛️ **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)** — **Entire Project Structure & Architecture**:
   - Multi-tier system architecture (Presentation, REST Controller, Service, DAO, and MySQL InnoDB).
   - End-to-end dataflow sequence diagrams and component interactions.
   - Concurrency & deadlock prevention model (eliminating circular wait).
   - Cloud deployment topology (Render Docker container + Aiven MySQL 8 cluster over TLS).

2. ☕ **[docs/JAVA.md](docs/JAVA.md)** — **Object-Oriented Programming (OOP) in Java 21**:
   - Comprehensive implementation of the **4 Pillars of OOP**: Abstraction (`Bookable`), Encapsulation, Inheritance, and Polymorphism (`PricingEngine`).
   - Software Design Patterns: Strategy Pattern, DAO Pattern, Chain of Responsibility, Factory, and Value Objects.
   - Rich Domain Logic: Behavior-rich `SeatType` enum and custom domain exception hierarchy.
   - Concurrency handling and curated Java OOP viva defense questions & answers.

3. 🐬 **[docs/DBMS.md](docs/DBMS.md)** — **Database Management Systems (DBMS in MySQL 8)**:
   - Full 15-table Relational Entity-Relationship (ER) diagram with cardinality.
   - Mathematical justification of Database Normalization (1NF, 2NF, 3NF) and historical price preservation.
   - Advanced database features: Stored Procedures (`sp_cancel_booking` for 80% refunds), Analytical Views with Window Functions (`v_top_selling_snacks` using `DENSE_RANK()`), Audit Triggers with JSON snapshots, and Scheduled Events.
   - Curated DBMS viva defense questions & answers.
