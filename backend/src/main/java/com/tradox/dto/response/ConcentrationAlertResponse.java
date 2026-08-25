package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcentrationAlertResponse {

    private String symbol;
    private String name;
    private BigDecimal currentValue;
    private BigDecimal percentage;
    private String riskLevel; // HIGH, MEDIUM
    private String message;
}
