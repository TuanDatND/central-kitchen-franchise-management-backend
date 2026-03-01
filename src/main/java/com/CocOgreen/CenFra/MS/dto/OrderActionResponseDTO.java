package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderActionResponseDTO {
    private Integer orderId;
    private String orderCode;
    private Integer storeId;
    private String storeName;
    private LocalDate deliveryDate;
    private StoreOrderStatus previousStatus;
    private StoreOrderStatus currentStatus;
    private OrderActionActorDTO actor;
    private LocalDateTime actionAt;
    private String cancelReason;
    private String message;
}
