package com.cinebook.domain.model;

import java.time.LocalDateTime;

/**
 * OOP Entity: AuditLogEntry.
 * 
 * Captures historical triggers and administrative events.
 */
public class AuditLogEntry {

    private Integer logId;
    private String tableName;
    private String actionType;
    private String recordId;
    private String detailsJson;
    private LocalDateTime createdAt;

    public AuditLogEntry() {
    }

    public AuditLogEntry(Integer logId, String tableName, String actionType, String recordId, String detailsJson, LocalDateTime createdAt) {
        this.logId = logId;
        this.tableName = tableName;
        this.actionType = actionType;
        this.recordId = recordId;
        this.detailsJson = detailsJson;
        this.createdAt = createdAt;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public void setDetailsJson(String detailsJson) {
        this.detailsJson = detailsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

