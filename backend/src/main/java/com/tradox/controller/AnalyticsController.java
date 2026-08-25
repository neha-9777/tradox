package com.tradox.controller;

import com.tradox.dto.response.AnalyticsDashboardResponse;
import com.tradox.dto.response.PerformanceTrendPointResponse;
import com.tradox.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Get complete analytics dashboard data for authenticated user.
     * GET /api/analytics/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsDashboardResponse> getAnalyticsDashboard(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AnalyticsDashboardResponse response = analyticsService.getAnalyticsDashboard(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Get historical portfolio performance trend curve points.
     * GET /api/analytics/performance-trend?period=1M|3M|6M|1Y|ALL
     */
    @GetMapping("/performance-trend")
    public ResponseEntity<List<PerformanceTrendPointResponse>> getPerformanceTrend(
            Authentication authentication,
            @RequestParam(name = "period", defaultValue = "1Y") String period) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<PerformanceTrendPointResponse> trend = analyticsService.getPerformanceTrend(userDetails.getUsername(), period);
        return ResponseEntity.ok(trend);
    }
}
