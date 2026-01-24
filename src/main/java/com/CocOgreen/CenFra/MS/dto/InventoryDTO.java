package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {

    private Integer inventoryId;

    private Integer locationId;
    private String locationName;

    private Integer itemId;
    private String itemName;
    private String sku;

    private BigDecimal quantity;
    private String batchCode;
    private LocalDate expDate;

    private LocalDateTime lastUpdated;
}
