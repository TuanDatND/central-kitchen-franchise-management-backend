package com.CocOgreen.CenFra.MS.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDashboardResponse {
    private Instant generatedAt;

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    private long totalStores;
    private long activeStores;
    private long inactiveStores;

    private long totalOrders;
    private long pendingOrders;
    private long approvedOrders;
    private long cancelledOrders;

    private long ordersToday;
    private long pendingOrdersToday;
    private long approvedOrdersToday;
    private long cancelledOrdersToday;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TopStoreSummary> topStores;

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TopStoreSummary {
        private Integer storeId;
        private String storeName;
        private long totalOrders;
    }
}
