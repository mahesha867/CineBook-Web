package com.cinebook.dao;

import com.cinebook.domain.model.AuditLogEntry;

import java.util.List;

public interface AuditLogDao {
    List<AuditLogEntry> findRecentLogs(int limit);
}

