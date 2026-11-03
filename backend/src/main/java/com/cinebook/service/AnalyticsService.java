package com.cinebook.service;

import com.cinebook.dao.AnalyticsDao;
import com.cinebook.dao.AuditLogDao;
import com.cinebook.domain.model.AuditLogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final AnalyticsDao analyticsDao;
    private final AuditLogDao auditLogDao;

    @Autowired
    public AnalyticsService(AnalyticsDao analyticsDao, AuditLogDao auditLogDao) {
        this.analyticsDao = analyticsDao;
        this.auditLogDao = auditLogDao;
    }

    public Map<String, Object> getKpiSummary() {
        return analyticsDao.getKpiSummary();
    }

    public List<Map<String, Object>> getMovieRevenueStats() {
        return analyticsDao.getMovieRevenueStats();
    }

    public List<Map<String, Object>> getShowOccupancyStats() {
        return analyticsDao.getShowOccupancyStats();
    }

    public List<Map<String, Object>> getTopSellingSnacks() {
        return analyticsDao.getTopSellingSnacks();
    }

    public List<Map<String, Object>> getPeakBookingHours() {
        return analyticsDao.getPeakBookingHours();
    }

    public List<AuditLogEntry> getRecentAuditLogs(int limit) {
        return auditLogDao.findRecentLogs(limit);
    }
}

