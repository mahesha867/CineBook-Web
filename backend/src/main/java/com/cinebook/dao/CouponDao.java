package com.cinebook.dao;

import com.cinebook.domain.model.Coupon;

import java.util.Optional;

public interface CouponDao {
    Optional<Coupon> findByCode(String code);
}

