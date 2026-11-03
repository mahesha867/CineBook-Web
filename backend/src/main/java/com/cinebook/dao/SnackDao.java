package com.cinebook.dao;

import com.cinebook.domain.model.Snack;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SnackDao {
    List<Snack> findAllAvailable();
    Optional<Snack> findById(String snackId);
    List<Snack> findByIds(List<String> snackIds);
    void updatePrice(String snackId, BigDecimal newPrice);
}

