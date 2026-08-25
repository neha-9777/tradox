package com.tradox.dto.response;

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
public class AssetResponse {

    private Long id;
    private String symbol;
    private String name;
    private AssetType assetType;
    private String exchange;
    private String sector;
    private String currency;
    private BigDecimal currentPrice;
}
