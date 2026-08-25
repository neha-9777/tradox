package com.tradox.broker.model;

import com.tradox.entity.Asset.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalizedBrokerHolding {

    private String symbol;
    private String name;
    private String exchange;
    private BigDecimal quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentPrice;
    private AssetType assetType;
    private String sector;
}
