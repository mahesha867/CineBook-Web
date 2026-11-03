package com.cinebook.service;

import com.cinebook.dao.SnackDao;
import com.cinebook.domain.model.Snack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SnackService {

    private final SnackDao snackDao;

    @Autowired
    public SnackService(SnackDao snackDao) {
        this.snackDao = snackDao;
    }

    public List<Snack> getAvailableSnacks() {
        return snackDao.findAllAvailable();
    }

    public Optional<Snack> getSnackById(String snackId) {
        return snackDao.findById(snackId);
    }

    public void updateSnackPrice(String snackId, BigDecimal newPrice) {
        snackDao.updatePrice(snackId, newPrice);
    }
}

