package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponse {

    private Long id;
    private String name;
    private String description;
    private String currency;
    private Boolean isDefault;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalPnL;
    private BigDecimal returnPercentage;
    private Integer totalHoldingsCount;
    private List<HoldingResponse> holdings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
