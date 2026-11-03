package com.cinebook.controller;

import com.cinebook.domain.model.AuditLogEntry;
import com.cinebook.dto.request.AdminLoginRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AnalyticsService analyticsService;

    @Value("${cinebook.admin.default-username:admin}")
    private String adminUser;

    @Value("${cinebook.admin.default-password:admin123}")
    private String adminPass;

    @Autowired
    public AdminController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody AdminLoginRequest req) {
        if (adminUser.equals(req.getUsername().trim()) && adminPass.equals(req.getPassword())) {
            return ResponseEntity.ok(ApiResponse.ok("Login successful", Map.of(
                    "role", "ADMIN",
                    "username", adminUser,
                    "token", "cinebook-admin-session-" + System.currentTimeMillis()
            )));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid administrator credentials"));
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKpiSummary() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getKpiSummary()));
    }

    @GetMapping("/analytics/movie-revenue")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMovieRevenue() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getMovieRevenueStats()));
    }

    @GetMapping("/analytics/occupancy")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getShowOccupancy() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getShowOccupancyStats()));
    }

    @GetMapping("/analytics/top-snacks")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopSnacks() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getTopSellingSnacks()));
    }

    @GetMapping("/analytics/peak-hours")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPeakHours() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getPeakBookingHours()));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogEntry>>> getAuditLogs(@RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getRecentAuditLogs(limit)));
    }
}

