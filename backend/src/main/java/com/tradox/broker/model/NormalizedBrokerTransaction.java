package com.tradox.broker.model;

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
public class NormalizedBrokerTransaction {

    private String externalTxId;
    private String symbol;
    private String name;
    private String exchange;
    private TransactionType type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal fee;
    private LocalDateTime transactionDate;
}
