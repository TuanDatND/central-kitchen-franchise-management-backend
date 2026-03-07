package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidatedOrderResponse;
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
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
                Date.from(request.getDeliveryDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

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
        if (hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            Store store = resolveStoreForStaff(auth.getName());
            orders = status == null
                    ? storeOrderRepository.findByStore_StoreId(store.getStoreId(), pageable)
                    : storeOrderRepository.findByStore_StoreIdAndStatus(store.getStoreId(), status, pageable);
        } else if (hasAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER)) {
            orders = status == null
                    ? storeOrderRepository.findAll(pageable)
                    : storeOrderRepository.findByStatus(status, pageable);
        } else {
            throw new AccessDeniedException("You do not have permission to view orders");
        }

        return orders.map(storeOrderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public StoreOrderDTO getOrderDetail(Integer orderId) {
        Authentication auth = getAuthentication();
        StoreOrder order = findOrder(orderId);

        if (hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            validateStoreStaffOwnership(order, auth.getName());
        } else if (!hasAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER)) {
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
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), null,
                "Order approved successfully");
    }

    @Transactional
    public OrderActionResponseDTO cancelOrder(Integer orderId, CancelOrderRequest request) {
        StoreOrder order = findOrder(orderId);
        validateCanceller();
        StoreOrderStatus previousStatus = order.getStatus();
        User actorUser = getCurrentUser(getAuthentication().getName());
        order.cancel();
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), request.getCancelReason(),
                "Order cancelled successfully");
    }

    @Transactional
    public ConsolidatedOrderResponse consolidateOrders(
            Integer productId,
            List<Integer> orderIds) {

        Authentication auth = getAuthentication();

        if (!hasAnyRole(auth, RoleName.SUPPLY_COORDINATOR)) {
            throw new AccessDeniedException("Only supply coordinator can consolidate orders");
        }

        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        List<StoreOrder> orders;
        List<Integer> consolidatedOrderIds;

        if (orderIds == null || orderIds.isEmpty()) {
            orders = storeOrderRepository.findDistinctByStatusAndProductId(StoreOrderStatus.APPROVED, productId);
            consolidatedOrderIds = orders.stream()
                    .map(StoreOrder::getOrderId)
                    .distinct()
                    .toList();
        } else {
            List<Integer> uniqueOrderIds = orderIds.stream()
                    .filter(id -> id != null && id > 0)
                    .distinct()
                    .toList();

            if (uniqueOrderIds.size() < 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least 2 valid unique orderIds are required");
            }

            orders = storeOrderRepository.findAllById(uniqueOrderIds);

            if (orders.size() != uniqueOrderIds.size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more orders do not exist");
            }

            boolean hasIneligibleOrders = orders.stream()
                    .anyMatch(order -> order.getStatus() != StoreOrderStatus.APPROVED);

            if (hasIneligibleOrders) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Only APPROVED orders can be consolidated"
                );
            }

            consolidatedOrderIds = uniqueOrderIds;
        }

        if (consolidatedOrderIds.size() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cần ít nhất 2 đơn APPROVED chứa sản phẩm này để gom đơn"
            );
        }

        int totalQuantity = 0;

        for (StoreOrder order : orders) {
            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getProduct().getProductId().equals(productId)) {
                    totalQuantity += detail.getQuantity();
                }
            }
        }

        if (totalQuantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không tìm thấy sản phẩm này trong các đơn được chọn để gom"
            );
        }

        orders.forEach(StoreOrder::markConsolidated);

        Instant suggestedStartDate = Instant.now();

        ConsolidatedOrderResponse.BasicInfo basicInfo =
                new ConsolidatedOrderResponse.BasicInfo(
                        LocalDateTime.now(),
                        auth.getName(),
                        orders.size(),
                        consolidatedOrderIds
                );

        return new ConsolidatedOrderResponse(
                productId,
                totalQuantity,
                suggestedStartDate,
                basicInfo
        );
    }

    private StoreOrder findOrder(Integer id) {
        return storeOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void validateApprover() {
        if (!hasAnyRole(getAuthentication(), RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER)) {
            throw new AccessDeniedException("Only supply coordinator or manager can approve order");
        }
    }

    private void validateCanceller() {
        if (!hasAnyRole(getAuthentication(), RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER)) {
            throw new AccessDeniedException("Only supply coordinator or manager can cancel order");
        }
    }

    private void validateStoreStaffOwnership(StoreOrder order, String username) {
        User user = getCurrentUser(username);
        Integer userStoreId = user.getStore() == null ? null : user.getStore().getStoreId();
        Integer orderStoreId = order.getStore() == null ? null : order.getStore().getStoreId();
        if (userStoreId == null || !userStoreId.equals(orderStoreId)) {
            throw new AccessDeniedException("You can only access your store orders");
        }
    }

    private boolean hasAnyRole(Authentication auth, RoleName... roles) {
        Set<String> roleSet = Set.of(roles).stream()
                .map(role -> ROLE_PREFIX + role.name())
                .collect(Collectors.toSet());
        return auth.getAuthorities().stream().anyMatch(a -> roleSet.contains(a.getAuthority()));
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Store resolveStoreForCreate(Authentication auth, Integer storeIdFromRequest) {
        if (hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            return resolveStoreForStaff(auth.getName());
        }
        throw new AccessDeniedException("You do not have permission to create order");
    }

    private Store resolveStoreForStaff(String username) {
        User user = getCurrentUser(username);
        if (user.getStore() == null) {
            throw new ResourceNotFoundException("Không tìm thấy cửa hàng cho tài khoản này");
        }
        return storeRepository.findById(user.getStore().getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng cho tài khoản này"));
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
                message);
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
