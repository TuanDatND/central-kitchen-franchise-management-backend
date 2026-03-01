package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreOrderDTO {

    private Integer orderId;
    private String orderCode;

    private Integer storeId;
    private String storeName;

    private LocalDateTime orderDate;
    private LocalDate deliveryDate;

    private StoreOrderStatus status;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrderDetailDTO> details;
}
