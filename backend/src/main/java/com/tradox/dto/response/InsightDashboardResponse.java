package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightDashboardResponse {

    private Integer overallHealthScore; // 0 to 100
    private String riskRating; // CONSERVATIVE, MODERATE, AGGRESSIVE
    private String executiveSummary;
    private List<InsightItemResponse> insights;
}
