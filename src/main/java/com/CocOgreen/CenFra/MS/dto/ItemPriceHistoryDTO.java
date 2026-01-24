package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPriceHistoryDTO {

    private Integer priceHistoryId;

    private Integer itemId;
    private String itemName;
    private String sku;

    private BigDecimal price;
    private LocalDateTime effectiveFrom;

    private Integer createdBy;
}
