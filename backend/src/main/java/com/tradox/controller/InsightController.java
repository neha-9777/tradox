package com.tradox.controller;

import com.tradox.dto.response.InsightDashboardResponse;
import com.tradox.service.InsightEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightEngineService insightEngineService;

    public InsightController(InsightEngineService insightEngineService) {
        this.insightEngineService = insightEngineService;
    }

    /**
     * Get complete intelligent insights dashboard for authenticated user.
     * GET /api/insights/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<InsightDashboardResponse> getInsightsDashboard(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        InsightDashboardResponse response = insightEngineService.generatePortfolioInsights(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}

