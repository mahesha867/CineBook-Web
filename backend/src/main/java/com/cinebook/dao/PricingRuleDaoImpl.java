package com.cinebook.dao;

import com.cinebook.domain.model.PricingRuleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PricingRuleDaoImpl implements PricingRuleDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PricingRuleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<PricingRuleEntity> RULE_ROW_MAPPER = (rs, rowNum) -> {
        PricingRuleEntity rule = new PricingRuleEntity();
        rule.setRuleId(rs.getInt("rule_id"));
        rule.setRuleName(rs.getString("rule_name"));
        rule.setRuleType(rs.getString("rule_type"));
        rule.setAdjustmentPercent(rs.getBigDecimal("adjustment_percent"));
        rule.setConditionThreshold(rs.getBigDecimal("condition_threshold"));
        rule.setActive(rs.getBoolean("is_active"));
        return rule;
    };

    @Override
    public List<PricingRuleEntity> findAllActive() {
        String sql = "SELECT * FROM pricing_rules WHERE is_active = TRUE ORDER BY rule_id ASC";
        return jdbcTemplate.query(sql, RULE_ROW_MAPPER);
    }
}

