package com.cinebook.controller;

import com.cinebook.domain.model.WaitlistEntry;
import com.cinebook.dto.request.JoinWaitlistRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.service.WaitlistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waitlist")
public class WaitlistController {

    private final WaitlistService waitlistService;

    @Autowired
    public WaitlistController(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WaitlistEntry>> joinWaitlist(@Valid @RequestBody JoinWaitlistRequest req) {
        WaitlistEntry entry = waitlistService.joinWaitlist(
                req.getShowId(),
                req.getCustomerName(),
                req.getCustomerEmail(),
                req.getCustomerPhone(),
                req.getSeatsRequested(),
                req.getPreferredTier()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Added to waitlist successfully", entry));
    }

    @GetMapping("/shows/{showId}")
    public ResponseEntity<ApiResponse<List<WaitlistEntry>>> getWaitlistForShow(@PathVariable int showId) {
        return ResponseEntity.ok(ApiResponse.ok(waitlistService.getWaitlistForShow(showId)));
    }
}

