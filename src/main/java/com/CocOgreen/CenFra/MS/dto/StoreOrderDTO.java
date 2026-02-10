package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreOrderDTO {

    private Integer orderId;
    private String orderCode;

    private Integer storeId;
    private String storeName;

    private LocalDateTime orderDate;
    private LocalDate deliveryDate;

    private StoreOrderStatus status; // PENDING, APPROVED, ...

    private List<OrderDetailDTO> details;
}
