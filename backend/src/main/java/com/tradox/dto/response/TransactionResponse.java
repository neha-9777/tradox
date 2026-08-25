package com.tradox.dto.response;

import com.tradox.entity.Transaction.TransactionType;
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
public class TransactionResponse {

    private Long id;
    private Long portfolioId;
    private AssetResponse asset;
    private TransactionType type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private BigDecimal fee;
    private LocalDateTime transactionDate;
    private String notes;
    private BigDecimal realizedPnL;
    private String externalTxId;
}
