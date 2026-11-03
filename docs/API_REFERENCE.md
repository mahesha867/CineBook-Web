# CineBook Web — REST API Reference

All requests and responses use the standard `application/json` content type and are wrapped in the standard `ApiResponse<T>` envelope:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-11-03T18:00:00"
}
```

---

## 1. Movies & Showtimes Endpoints

### `GET /api/movies`
Returns a list of all active cinema movies.

### `GET /api/movies/{movieId}`
Returns metadata for a specific movie.

### `GET /api/movies/{movieId}/shows`
Returns scheduled screenings for a movie with screen names and price multipliers.

---

## 2. Interactive Seat Map & Hold Endpoints

### `GET /api/shows/{showId}/seats?sessionId={sessionId}`
Returns the 2D auditorium seat grid.
- **Statuses**: `AVAILABLE`, `HELD`, `HELD_BY_YOU`, `BOOKED`.
- **Hold Expiry**: Returns `holdExpiresInSeconds` for active holds.

### `POST /api/shows/{showId}/hold-seats`
Temporarily locks seats for 5 minutes.
- **Request Body**:
```json
{
  "seatIds": [1, 2],
  "sessionId": "sess_xyz123"
}
```
- **Response**: HTTP 200 OK or HTTP 423 LOCKED / HTTP 409 CONFLICT.

### `POST /api/shows/{showId}/release-holds`
Explicitly frees holds when a user backs out.

### `POST /api/shows/{showId}/recommend-seats`
Executes backend recommendation strategy.
- **Request Body**:
```json
{
  "count": 2,
  "preferredTier": "VIP",
  "strategy": "CENTER"
}
```

---

## 3. Concessions & Coupons Endpoints

### `GET /api/snacks`
Returns the active concessions menu (S001 - S009) with categories and prices.

### `POST /api/coupons/validate`
Calculates transparent price breakdown.
- **Request Body**:
```json
{
  "showId": 1,
  "seatIds": [1, 2],
  "snackOrders": { "S001": 2, "S007": 1 },
  "couponCode": "CINE10"
}
```
- **Response Data**: Returns itemized `PriceBreakdown` (Base tickets, Dynamic surcharges, Ticket subtotal, Coupon discount, Snacks total, Grand total).

---

## 4. Bookings & Reservations Endpoints

### `POST /api/bookings`
Confirms reservation atomically under `REPEATABLE READ` transaction with deterministic row locks.
- **Request Body**:
```json
{
  "showId": 1,
  "seatIds": [1, 2],
  "snackOrders": { "S001": 2 },
  "couponCode": "CINE10",
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "9876543210",
  "sessionId": "sess_xyz123"
}
```
- **Response**: HTTP 201 Created with booking details and Base64 ZXing QR code data URL.

### `GET /api/bookings/{bookingId}`
Returns booking information and QR code for boarding pass.

### `POST /api/bookings/{bookingId}/cancel`
Cancels booking, calculates 80% refund, frees seats, and auto-promotes waitlisted customers.

### `GET /api/bookings/lookup?query={phoneOrEmailOrBookingId}`
Finds past reservations by booking ID, customer phone, or email.

---

## 5. Waitlist Endpoints

### `POST /api/waitlist`
Joins queue for a sold-out show.
- **Request Body**:
```json
{
  "showId": 1,
  "customerName": "Jane Smith",
  "customerEmail": "jane@example.com",
  "customerPhone": "9876543211",
  "seatsRequested": 2,
  "preferredTier": "VIP"
}
```

### `GET /api/waitlist/shows/{showId}`
Returns current queue positions for that screening.

---

## 6. Admin Analytics Endpoints

### `POST /api/admin/login`
Authenticates administrator (`admin` / `admin123`).

### `GET /api/admin/analytics/summary`
Returns high-level KPI metrics (net revenue, confirmed count, cancellation rate, average occupancy).

### `GET /api/admin/analytics/movie-revenue`
Returns view data from `v_movie_revenue_analytics`.

### `GET /api/admin/analytics/occupancy`
Returns view data from `v_show_occupancy`.

### `GET /api/admin/analytics/top-snacks`
Returns window-function ranked concessions from `v_top_selling_snacks`.

### `GET /api/admin/analytics/peak-hours`
Returns hourly booking distribution.

### `GET /api/admin/audit-logs?limit=50`
Returns database trigger audit trail entries.

