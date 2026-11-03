package com.cinebook.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AnalyticsDao {
    Map<String, Object> getKpiSummary();
    List<Map<String, Object>> getMovieRevenueStats();
    List<Map<String, Object>> getShowOccupancyStats();
    List<Map<String, Object>> getTopSellingSnacks();
    List<Map<String, Object>> getPeakBookingHours();
}

