/**
 * CineBook Web - Frontend Client Application Helper
 */

const CineBook = {
  // Session management for 5-minute seat holds
  getSessionId() {
    let sid = sessionStorage.getItem("cinebook_session_id");
    if (!sid) {
      sid = "sess_" + Math.random().toString(36).substring(2, 15) + Date.now().toString(36);
      sessionStorage.setItem("cinebook_session_id", sid);
    }
    return sid;
  },

  // API Client wrapper
  async fetchApi(url, options = {}) {
    try {
      const resp = await fetch(url, {
        headers: {
          "Content-Type": "application/json",
          ...options.headers,
        },
        ...options,
      });

      const json = await resp.json();
      if (!resp.ok) {
        throw new Error(json.message || `Request failed with status ${resp.status}`);
      }
      return json;
    } catch (err) {
      console.error("API error:", err);
      throw err;
    }
  },

  // Booking Cart State stored in sessionStorage
  getBookingState() {
    const raw = sessionStorage.getItem("cinebook_booking_state");
    return raw ? JSON.parse(raw) : {
      movieId: null,
      movieTitle: null,
      showId: null,
      showTime: null,
      screenName: null,
      seatIds: [],
      seatLabels: [],
      snackOrders: {}, // snackId -> qty
      couponCode: null,
      priceBreakdown: null,
    };
  },

  saveBookingState(state) {
    sessionStorage.setItem("cinebook_booking_state", JSON.stringify(state));
  },

  clearBookingState() {
    sessionStorage.removeItem("cinebook_booking_state");
  },

  // Formatting helpers
  formatCurrency(val) {
    const num = Number(val) || 0;
    return "Rs. " + num.toFixed(2);
  },

  formatDateTime(isoStr) {
    if (!isoStr) return "";
    const d = new Date(isoStr);
    return d.toLocaleString("en-IN", {
      weekday: "short",
      day: "numeric",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  },

  showToast(message, type = "info") {
    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    toast.style.cssText = `
      position: fixed;
      bottom: 24px;
      right: 24px;
      background: ${type === "error" ? "rgba(244, 63, 94, 0.95)" : type === "success" ? "rgba(16, 185, 129, 0.95)" : "rgba(6, 182, 212, 0.95)"};
      color: #fff;
      padding: 12px 20px;
      border-radius: 8px;
      box-shadow: 0 10px 25px rgba(0,0,0,0.5);
      z-index: 9999;
      font-weight: 600;
      font-size: 0.95rem;
      animation: fadeIn 0.3s ease;
    `;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = "0";
      toast.style.transition = "opacity 0.5s ease";
      setTimeout(() => toast.remove(), 500);
    }, 4000);
  }
};

