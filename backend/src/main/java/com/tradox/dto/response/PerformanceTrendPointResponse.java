package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceTrendPointResponse {
    private String label;
    private LocalDate date;
    private BigDecimal value;
    private BigDecimal invested;
    private BigDecimal pnl;
}
