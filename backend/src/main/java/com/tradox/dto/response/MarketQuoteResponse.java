package com.tradox.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketQuoteResponse {

    private String symbol;
    private String name;
    private String exchange;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private BigDecimal changeAmount;
    private BigDecimal changePercentage;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private Long volume;
    private String dataSource;
    private LocalDateTime timestamp;
}
