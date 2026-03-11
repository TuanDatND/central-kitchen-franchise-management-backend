package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.AdminDashboardResponse;
import com.CocOgreen.CenFra.MS.enums.StoreStatus;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.enums.UserStatus;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.TopStoreOrderProjection;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final StoreOrderRepository storeOrderRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getOverview(int topStoresLimit) {
        int safeLimit = Math.min(Math.max(topStoresLimit, 1), 20);

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveUsers = userRepository.countByStatus(UserStatus.INACTIVE);

        long totalStores = storeRepository.count();
        long activeStores = storeRepository.countByStatus(StoreStatus.ACTIVE);
        long inactiveStores = storeRepository.countByStatus(StoreStatus.INACTIVE);

        long totalOrders = storeOrderRepository.count();
        long pendingOrders = storeOrderRepository.countByStatus(StoreOrderStatus.PENDING);
        long approvedOrders = storeOrderRepository.countByStatus(StoreOrderStatus.APPROVED);
        long cancelledOrders = storeOrderRepository.countByStatus(StoreOrderStatus.CANCELLED);

        LocalDateTime todayStart = startOfToday();
        LocalDateTime tomorrowStart = startOfTomorrow();
        long ordersToday = storeOrderRepository.countByOrderDateBetween(todayStart, tomorrowStart);
        long pendingOrdersToday = storeOrderRepository.countByStatusAndOrderDateBetween(StoreOrderStatus.PENDING, todayStart, tomorrowStart);
        long approvedOrdersToday = storeOrderRepository.countByStatusAndOrderDateBetween(StoreOrderStatus.APPROVED, todayStart, tomorrowStart);
        long cancelledOrdersToday = storeOrderRepository.countByStatusAndOrderDateBetween(StoreOrderStatus.CANCELLED, todayStart, tomorrowStart);

        List<AdminDashboardResponse.TopStoreSummary> topStores = storeOrderRepository
                .findTopStoresByOrderCount(PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toTopStoreSummary)
                .toList();

        return new AdminDashboardResponse(
                Instant.now(),
                totalUsers,
                activeUsers,
                inactiveUsers,
                totalStores,
                activeStores,
                inactiveStores,
                totalOrders,
                pendingOrders,
                approvedOrders,
                cancelledOrders,
                ordersToday,
                pendingOrdersToday,
                approvedOrdersToday,
                cancelledOrdersToday,
                topStores
        );
    }

    private AdminDashboardResponse.TopStoreSummary toTopStoreSummary(TopStoreOrderProjection projection) {
        return new AdminDashboardResponse.TopStoreSummary(
                projection.getStoreId(),
                projection.getStoreName(),
                projection.getTotalOrders()
        );
    }

    private LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime startOfTomorrow() {
        return LocalDate.now().plusDays(1).atStartOfDay();
    }
}
