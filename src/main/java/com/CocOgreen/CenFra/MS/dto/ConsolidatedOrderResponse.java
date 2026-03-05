package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidatedOrderResponse {
    private BasicInfo basicInfo;
    private List<ManufacturingRequestBody> manufacturingOrders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo {
        private LocalDateTime consolidatedAt;
        private String consolidatedBy;
        private int totalOrders;
        private List<Integer> orderIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ManufacturingRequestBody {
        private Integer productId;
        private Integer quantity;
        private Instant startDate;
    }
}
