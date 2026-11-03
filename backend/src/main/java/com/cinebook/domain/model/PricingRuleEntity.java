package com.cinebook.domain.model;

import java.math.BigDecimal;

/**
 * OOP Entity: PricingRuleEntity.
 * 
 * Represents dynamic surcharge and discount configuration rules stored in the database.
 */
public class PricingRuleEntity {

    private Integer ruleId;
    private String ruleName;
    private String ruleType; // WEEKEND_SURCHARGE, LATE_NIGHT_DISCOUNT, HIGH_OCCUPANCY_SURCHARGE
    private BigDecimal adjustmentPercent;
    private BigDecimal conditionThreshold;
    private boolean isActive = true;

    public PricingRuleEntity() {
    }

    public PricingRuleEntity(Integer ruleId, String ruleName, String ruleType, BigDecimal adjustmentPercent, BigDecimal conditionThreshold, boolean isActive) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.adjustmentPercent = adjustmentPercent;
        this.conditionThreshold = conditionThreshold;
        this.isActive = isActive;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public BigDecimal getAdjustmentPercent() {
        return adjustmentPercent;
    }

    public void setAdjustmentPercent(BigDecimal adjustmentPercent) {
        this.adjustmentPercent = adjustmentPercent;
    }

    public BigDecimal getConditionThreshold() {
        return conditionThreshold;
    }

    public void setConditionThreshold(BigDecimal conditionThreshold) {
        this.conditionThreshold = conditionThreshold;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

