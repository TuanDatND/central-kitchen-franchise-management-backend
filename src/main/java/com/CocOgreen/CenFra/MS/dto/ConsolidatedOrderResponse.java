package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidatedOrderResponse {
    private LocalDateTime consolidatedAt;
    private String consolidatedBy;
    private int totalOrders;
    private List<Integer> orderIds;
    private List<ProductGroup> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductGroup {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private List<Integer> orderIds;
    }
}
