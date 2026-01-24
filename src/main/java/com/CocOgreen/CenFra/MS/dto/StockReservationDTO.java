package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockReservationDTO {
    private Integer reservationID;
    private Integer itemId;
    private String itemName;
    private Integer locationId;
    private BigDecimal reservedQty;
    private String refType; // "PROD_ORDER"
    private Integer refId;  // ID của ProductionOrder tương ứng
    private LocalDateTime createdAt;
}
