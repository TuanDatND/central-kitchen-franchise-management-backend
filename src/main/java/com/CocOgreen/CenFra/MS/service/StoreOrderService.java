package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.CreateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.dto.OrderActionActorDTO;
import com.CocOgreen.CenFra.MS.dto.OrderActionResponseDTO;
import com.CocOgreen.CenFra.MS.dto.OrderLineRequest;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.mapper.StoreOrderMapper;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.TopStoreOrderProjection;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreOrderService {
    private static final String ROLE_PREFIX = "ROLE_";

    private final StoreOrderRepository storeOrderRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StoreOrderMapper storeOrderMapper;

    @Transactional
    public StoreOrderDTO createOrder(CreateStoreOrderRequest request) {
        Authentication auth = getAuthentication();
        Store store = resolveStoreForCreate(auth, request.getStoreId());
        if (!Boolean.TRUE.equals(store.getIsActive())) {
            throw new IllegalArgumentException("Store is inactive");
        }
        if (request.getDeliveryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Delivery date must be today or in the future");
        }

        StoreOrder order = new StoreOrder(
                generateOrderCode(),
                store,
                Date.from(request.getDeliveryDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
        );

        Map<Integer, Product> productMap = resolveProducts(request.getDetails());
        for (OrderLineRequest line : request.getDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setProduct(productMap.get(line.getProductId()));
            detail.setQuantity(line.getQuantity());
            order.addOrderDetail(detail);
        }

        StoreOrder saved = storeOrderRepository.save(order);
        return storeOrderMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<StoreOrderDTO> listOrders(StoreOrderStatus status, int page, int size) {
        Authentication auth = getAuthentication();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));

        Page<StoreOrder> orders;
        if (hasAnyRole(auth, RoleName.STORE_MANAGER)) {
            Store store = resolveStoreByManager(auth.getName());
            orders = status == null
                    ? storeOrderRepository.findByStore_StoreId(store.getStoreId(), pageable)
                    : storeOrderRepository.findByStore_StoreIdAndStatus(store.getStoreId(), status, pageable);
        } else if (hasAnyRole(auth, RoleName.COORDINATOR, RoleName.ADMIN)) {
            orders = status == null
                    ? storeOrderRepository.findAll(pageable)
                    : storeOrderRepository.findByStatus(status, pageable);
        } else {
            throw new AccessDeniedException("You do not have permission to view orders");
        }

        return orders.map(storeOrderMapper::toSummaryDTO);
    }

    @Transactional(readOnly = true)
    public StoreOrderDTO getOrderDetail(Integer orderId) {
        Authentication auth = getAuthentication();
        StoreOrder order = findOrder(orderId);

        if (hasAnyRole(auth, RoleName.STORE_MANAGER)) {
            String managerUsername = order.getStore().getManager().getUserName();
            if (!managerUsername.equals(auth.getName())) {
                throw new AccessDeniedException("You can only view your store orders");
            }
        } else if (!hasAnyRole(auth, RoleName.COORDINATOR, RoleName.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return storeOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopStoresByOrderCount(int limit) {
        if (limit < 1 || limit > 50) {
            throw new IllegalArgumentException("limit must be between 1 and 50");
        }
        return storeOrderRepository.findTopStoresByOrderCount(PageRequest.of(0, limit))
                .stream()
                .map(this::toDashboardRow)
                .toList();
    }

    @Transactional
    public OrderActionResponseDTO approveOrder(Integer orderId) {
        StoreOrder order = findOrder(orderId);
        validateApprover();
        StoreOrderStatus previousStatus = order.getStatus();
        User actorUser = getCurrentUser(getAuthentication().getName());
        order.approve();
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), null, "Order approved successfully");
    }

    @Transactional
    public OrderActionResponseDTO cancelOrder(Integer orderId, CancelOrderRequest request) {
        StoreOrder order = findOrder(orderId);
        validateOwnershipOrCoordinatorOrAdmin(order);
        StoreOrderStatus previousStatus = order.getStatus();
        User actorUser = getCurrentUser(getAuthentication().getName());
        order.cancel();
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), request.getCancelReason(), "Order cancelled successfully");
    }

    private StoreOrder findOrder(Integer id) {
        return storeOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void validateApprover() {
        if (!hasAnyRole(getAuthentication(), RoleName.COORDINATOR, RoleName.ADMIN)) {
            throw new AccessDeniedException("Only coordinator or admin can approve order");
        }
    }

    private void validateOwnershipOrCoordinatorOrAdmin(StoreOrder order) {
        Authentication auth = getAuthentication();
        if (hasAnyRole(auth, RoleName.COORDINATOR, RoleName.ADMIN)) {
            return;
        }
        if (!hasAnyRole(auth, RoleName.STORE_MANAGER)) {
            throw new AccessDeniedException("You do not have permission to cancel this order");
        }
        if (!order.getStore().getManager().getUserName().equals(auth.getName())) {
            throw new AccessDeniedException("You can only cancel your own store order");
        }
    }

    private boolean hasAnyRole(Authentication auth, RoleName... roles) {
        Set<String> roleSet = Set.of(roles).stream()
                .map(role -> ROLE_PREFIX + role.name())
                .collect(Collectors.toSet());
        return auth.getAuthorities().stream().anyMatch(a -> roleSet.contains(a.getAuthority()));
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Store resolveStoreForCreate(Authentication auth, Integer storeIdFromRequest) {
        if (hasAnyRole(auth, RoleName.STORE_MANAGER)) {
            return resolveStoreByManager(auth.getName());
        }
        if (hasAnyRole(auth, RoleName.ADMIN)) {
            if (storeIdFromRequest == null) {
                throw new IllegalArgumentException("storeId is required for admin");
            }
            return storeRepository.findById(storeIdFromRequest).orElseThrow(() -> new RuntimeException("Store not found"));
        }
        throw new AccessDeniedException("You do not have permission to create order");
    }

    private Store resolveStoreByManager(String username) {
        User user = getCurrentUser(username);
        return storeRepository.findByManager_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Store not found for this manager"));
    }

    private Map<Integer, Product> resolveProducts(List<OrderLineRequest> details) {
        Set<Integer> productIds = new HashSet<>();
        for (OrderLineRequest line : details) {
            if (!productIds.add(line.getProductId())) {
                throw new IllegalArgumentException("Duplicate product in order: " + line.getProductId());
            }
        }

        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("One or more products do not exist");
        }
        return products.stream().collect(Collectors.toMap(Product::getProductId, p -> p));
    }

    private String generateOrderCode() {
        return "SO-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private Map<String, Object> toDashboardRow(TopStoreOrderProjection projection) {
        Map<String, Object> row = new HashMap<>();
        row.put("storeId", projection.getStoreId());
        row.put("storeName", projection.getStoreName());
        row.put("totalOrders", projection.getTotalOrders());
        return row;
    }

    private OrderActionResponseDTO buildActionResponse(StoreOrder order,
                                                       StoreOrderStatus previousStatus,
                                                       User actorUser,
                                                       LocalDateTime actionAt,
                                                       String cancelReason,
                                                       String message) {
        LocalDate deliveryDate = toLocalDate(order.getDeliveryDate());
        String fullName = actorUser.getFullName();
        if (fullName == null || fullName.isBlank()) {
            fullName = actorUser.getUserName();
        }
        OrderActionActorDTO actor = new OrderActionActorDTO(actorUser.getUserId(), actorUser.getUserName(), fullName);
        return new OrderActionResponseDTO(
                order.getOrderId(),
                order.getOrderCode(),
                order.getStore().getStoreId(),
                order.getStore().getStoreName(),
                deliveryDate,
                previousStatus,
                order.getStatus(),
                actor,
                actionAt,
                cancelReason,
                message
        );
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
