package com.CocOgreen.CenFra.MS.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailDTO {
    private Integer orderDetailId;
    private Integer productId;
    private String productName;
    private String unitName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
