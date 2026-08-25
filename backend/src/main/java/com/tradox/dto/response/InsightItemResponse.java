package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightItemResponse {

    private String id;
    private InsightCategory category; // SUMMARY, CONCENTRATION, PERFORMANCE, TREND, RISK, AI_COMMENTARY
    private InsightSeverity severity; // INFO, SUCCESS, WARNING, DANGER
    private String title;
    private String description;
    private String actionableAdvice;
    private String symbol;
    private Integer score;

    public enum InsightCategory {
        SUMMARY, CONCENTRATION, PERFORMANCE, TREND, RISK, AI_COMMENTARY
    }

    public enum InsightSeverity {
        INFO, SUCCESS, WARNING, DANGER
    }
}
