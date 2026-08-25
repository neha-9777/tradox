package com.tradox.dto.request;

import com.tradox.entity.Transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TransactionRequest {

    @NotBlank(message = "Asset symbol is required")
    private String symbol;

    private String assetName;

    private String exchange;

    @NotNull(message = "Transaction type (BUY/SELL) is required")
    private TransactionType type;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal fee;

    private LocalDateTime transactionDate;

    private String notes;

    private String externalTxId;
}
