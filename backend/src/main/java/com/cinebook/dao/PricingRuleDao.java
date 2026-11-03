package com.cinebook.dao;

import com.cinebook.domain.model.PricingRuleEntity;

import java.util.List;

public interface PricingRuleDao {
    List<PricingRuleEntity> findAllActive();
}

