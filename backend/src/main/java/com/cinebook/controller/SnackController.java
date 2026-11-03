package com.cinebook.controller;

import com.cinebook.domain.model.Snack;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.service.SnackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/snacks")
public class SnackController {

    private final SnackService snackService;

    @Autowired
    public SnackController(SnackService snackService) {
        this.snackService = snackService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Snack>>> getAvailableSnacks() {
        return ResponseEntity.ok(ApiResponse.ok(snackService.getAvailableSnacks()));
    }
}

