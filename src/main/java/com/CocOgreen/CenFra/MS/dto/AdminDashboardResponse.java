package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
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

    private List<TopStoreSummary> topStores;

    @Data
    @AllArgsConstructor
    public static class TopStoreSummary {
        private Integer storeId;
        private String storeName;
        private long totalOrders;
    }
}
