package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {

    private Integer itemId;
    private String itemName;
    private String sku;

    private Integer itemTypeId;
    private String itemTypeCode;

    private Integer unitId;
    private String unitName;

    private BigDecimal minStock;
    private Integer shelfLifeDays;

    private Boolean isDeleted;
}
