package com.cinebook.dao;

import com.cinebook.domain.model.AuditLogEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogDaoImpl implements AuditLogDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AuditLogDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<AuditLogEntry> AUDIT_ROW_MAPPER = (rs, rowNum) -> {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setLogId(rs.getInt("log_id"));
        entry.setTableName(rs.getString("table_name"));
        entry.setActionType(rs.getString("action_type"));
        entry.setRecordId(rs.getString("record_id"));
        entry.setDetailsJson(rs.getString("details"));
        entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return entry;
    };

    @Override
    public List<AuditLogEntry> findRecentLogs(int limit) {
        String sql = "SELECT * FROM audit_log ORDER BY log_id DESC LIMIT ?";
        return jdbcTemplate.query(sql, AUDIT_ROW_MAPPER, limit > 0 ? limit : 50);
    }
}

