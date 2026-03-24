package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryIssueItemRequest {

    @NotNull(message = "productId không được để trống")
    private Integer productId;

    @Min(value = 1, message = "quantity phải lớn hơn 0")
    private Integer quantity;

    @Min(value = 0, message = "receivedQuantity không được nhỏ hơn 0")
    private Integer receivedQuantity;
}
