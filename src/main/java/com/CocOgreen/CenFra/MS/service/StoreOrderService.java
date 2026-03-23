package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidatedOrderResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.dto.OrderActionActorDTO;
import com.CocOgreen.CenFra.MS.dto.OrderActionResponseDTO;
import com.CocOgreen.CenFra.MS.dto.OrderLineRequest;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.StoreStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Store store = resolveStoreForCreate(auth);
        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new IllegalArgumentException("Store is inactive");
        }
        validateDeliveryDate(request.getDeliveryDate());

        StoreOrder order = new StoreOrder(
                generateOrderCode(),
                store,
                request.getDeliveryDate());

        Map<Integer, Product> productMap = resolveProducts(request.getDetails());
        for (OrderLineRequest line : request.getDetails()) {
            order.addOrderDetail(createOrderDetail(productMap.get(line.getProductId()), line.getQuantity()));
        }

        StoreOrder saved = storeOrderRepository.save(order);
        return storeOrderMapper.toDTO(saved);
    }

    @Transactional
    public StoreOrderDTO updateOrder(Integer orderId, UpdateStoreOrderRequest request) {
        Authentication auth = getAuthentication();
        validateOrderEditor(auth);

        StoreOrder order = findOrder(orderId);
        validateStoreStaffOwnership(order, auth.getName());

        if (order.getStatus() != StoreOrderStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được chỉnh sửa đơn hàng khi đơn đang ở trạng thái PENDING");
        }

        validateDeliveryDate(request.getDeliveryDate());

        Map<Integer, Product> productMap = resolveProducts(request.getDetails());
        order.setDeliveryDate(request.getDeliveryDate());
        order.getOrderDetails().clear();

        for (OrderLineRequest line : request.getDetails()) {
            order.addOrderDetail(createOrderDetail(productMap.get(line.getProductId()), line.getQuantity()));
        }

        return storeOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<StoreOrderDTO> listOrders(StoreOrderStatus status, int page, int size) {
        Authentication auth = getAuthentication();
        Page<StoreOrder> orders;
        if (hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
            Store store = resolveStoreForStaff(auth.getName());
            orders = status == null
                    ? storeOrderRepository.findByStore_StoreId(store.getStoreId(), pageable)
                    : storeOrderRepository.findByStore_StoreIdAndStatus(store.getStoreId(), status, pageable);
        } else if (hasAnyRole(auth, RoleName.SUPPLY_COORDINATOR, RoleName.MANAGER)) {
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.ASC, "deliveryDate")
                            .and(Sort.by(Sort.Direction.ASC, "orderDate")));
            orders = status == null
                    ? storeOrderRepository.findAll(pageable)
                    : storeOrderRepository.findByStatus(status, pageable);
        } else {
            throw new AccessDeniedException("You do not have permission to view orders");
        }

        return orders.map(order -> toOrderDto(order, auth));
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

        return toOrderDto(order, auth);
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
        Authentication auth = getAuthentication();
        validateCanceller(auth);
        validateStoreStaffOwnership(order, auth.getName());
        StoreOrderStatus previousStatus = order.getStatus();
        User actorUser = getCurrentUser(auth.getName());
        order.cancel();
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), request.getCancelReason(),
                "Order cancelled successfully");
    }

    @Transactional
    public OrderActionResponseDTO receiveOrder(Integer orderId) {
        StoreOrder order = findOrder(orderId);
        Authentication auth = getAuthentication();
        validateStoreStaffReceiver(auth);
        validateStoreStaffOwnership(order, auth.getName());

        if (order.getStatus() != StoreOrderStatus.IN_TRANSIT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được xác nhận nhận hàng khi đơn đang ở trạng thái IN_TRANSIT");
        }

        StoreOrderStatus previousStatus = order.getStatus();
        User actorUser = getCurrentUser(auth.getName());
        order.markAsReceived();
        return buildActionResponse(order, previousStatus, actorUser, LocalDateTime.now(), null,
                "Xác nhận nhận hàng thành công");
    }

    @Transactional
    public ConsolidatedOrderResponse consolidateOrdersAutomatically() {
        Authentication auth = getAuthentication();
        validateConsolidator(auth);

        List<StoreOrder> orders = storeOrderRepository.findDistinctByStatusWithDetails(StoreOrderStatus.APPROVED);
        return consolidateEligibleOrders(orders, auth, "Cần ít nhất 2 đơn APPROVED để gom tự động");
    }

    @Transactional
    public ConsolidatedOrderResponse consolidateOrdersManually(List<Integer> orderIds) {
        Authentication auth = getAuthentication();
        validateConsolidator(auth);

        List<Integer> uniqueOrderIds = orderIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (uniqueOrderIds.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần ít nhất 2 orderIds hợp lệ để gom thủ công");
        }

        List<StoreOrder> orders = storeOrderRepository.findDistinctByOrderIdInWithDetails(uniqueOrderIds);

        if (orders.size() != uniqueOrderIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Một hoặc nhiều đơn hàng không tồn tại");
        }

        return consolidateEligibleOrders(orders, auth, "Cần ít nhất 2 đơn APPROVED để gom thủ công");
    }

    @Transactional
    public Map<String, Object> cancelConsolidation(List<Integer> orderIds) {
        Authentication auth = getAuthentication();
        validateConsolidator(auth);

        List<Integer> uniqueOrderIds = orderIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();

        if (uniqueOrderIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cần ít nhất 1 orderId hợp lệ để hủy gom đơn");
        }

        List<StoreOrder> orders = storeOrderRepository.findAllById(uniqueOrderIds);
        if (orders.size() != uniqueOrderIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Một hoặc nhiều đơn hàng không tồn tại");
        }

        List<Integer> invalidOrderIds = orders.stream()
                .filter(order -> order.getStatus() != StoreOrderStatus.CONSOLIDATED)
                .map(StoreOrder::getOrderId)
                .sorted()
                .toList();

        if (!invalidOrderIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Chỉ được hủy gom các đơn đang ở trạng thái CONSOLIDATED. Invalid orderIds: " + invalidOrderIds);
        }

        orders.forEach(order -> order.setStatus(StoreOrderStatus.APPROVED));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cancelledAt", LocalDateTime.now());
        response.put("cancelledBy", auth.getName());
        response.put("totalOrders", uniqueOrderIds.size());
        response.put("orderIds", uniqueOrderIds.stream().sorted().toList());
        response.put("currentStatus", StoreOrderStatus.APPROVED);
        return response;
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

    private void validateCanceller(Authentication auth) {
        if (!hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            throw new AccessDeniedException("Only franchise store staff can cancel order");
        }
    }

    private void validateStoreStaffReceiver(Authentication auth) {
        if (!hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            throw new AccessDeniedException("Only franchise store staff can confirm receiving order");
        }
    }

    private void validateOrderEditor(Authentication auth) {
        if (!hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF)) {
            throw new AccessDeniedException("Only franchise store staff can update order");
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

    private void validateConsolidator(Authentication auth) {
        if (!hasAnyRole(auth, RoleName.SUPPLY_COORDINATOR)) {
            throw new AccessDeniedException("Only supply coordinator can consolidate orders");
        }
    }

    private ConsolidatedOrderResponse consolidateEligibleOrders(
            List<StoreOrder> orders,
            Authentication auth,
            String minimumOrdersMessage) {

        List<StoreOrder> eligibleOrders = orders.stream()
                .filter(order -> order.getStatus() == StoreOrderStatus.APPROVED)
                .toList();

        if (eligibleOrders.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, minimumOrdersMessage);
        }

        Map<Integer, ConsolidatedAccumulator> groupedProducts = new LinkedHashMap<>();

        for (StoreOrder order : eligibleOrders) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                ConsolidatedAccumulator accumulator = groupedProducts.computeIfAbsent(
                        product.getProductId(),
                        ignored -> new ConsolidatedAccumulator(
                                product.getProductId(),
                                product.getProductName()
                        )
                );
                accumulator.add(order.getOrderId(), detail.getQuantity());
            }
        }

        if (groupedProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có sản phẩm nào để gom");
        }

        eligibleOrders.forEach(StoreOrder::markConsolidated);

        List<Integer> consolidatedOrderIds = eligibleOrders.stream()
                .map(StoreOrder::getOrderId)
                .distinct()
                .toList();

        List<ConsolidatedOrderResponse.ProductGroup> products = groupedProducts.values().stream()
                .map(ConsolidatedAccumulator::toResponse)
                .toList();

        return new ConsolidatedOrderResponse(
                LocalDateTime.now(),
                auth.getName(),
                consolidatedOrderIds.size(),
                consolidatedOrderIds,
                products
        );
    }

    private static final class ConsolidatedAccumulator {
        private final Integer productId;
        private final String productName;
        private int quantity;
        private final Set<Integer> orderIds = new HashSet<>();

        private ConsolidatedAccumulator(Integer productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        private void add(Integer orderId, Integer additionalQuantity) {
            quantity += additionalQuantity;
            orderIds.add(orderId);
        }

        private ConsolidatedOrderResponse.ProductGroup toResponse() {
            return new ConsolidatedOrderResponse.ProductGroup(
                    productId,
                    productName,
                    quantity,
                    orderIds.stream().sorted().toList()
            );
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

    private Store resolveStoreForCreate(Authentication auth) {
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

    private OrderDetail createOrderDetail(Product product, Integer quantity) {
        int multiplier = product.getOrderMultiplier() != null ? product.getOrderMultiplier() : 1;
        if (quantity % multiplier != 0) {
            throw new IllegalArgumentException(String.format("Số lượng đặt hàng không hợp lệ cho sản phẩm '%s'. Số lượng phải là bội số của %d.", product.getProductName(), multiplier));
        }

        OrderDetail detail = new OrderDetail();
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setUnitPrice(resolveProductPrice(product));
        return detail;
    }

    private BigDecimal resolveProductPrice(Product product) {
        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Product price is missing: " + product.getProductId());
        }
        return product.getPrice();
    }

    private void validateDeliveryDate(LocalDate deliveryDate) {
        if (deliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Delivery date must be today or in the future");
        }
    }

    private String generateOrderCode() {
        return "SO-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private StoreOrderDTO toOrderDto(StoreOrder order, Authentication auth) {
        StoreOrderDTO dto = storeOrderMapper.toDTO(order);
        if (hasAnyRole(auth, RoleName.FRANCHISE_STORE_STAFF) && dto.getStatus() == StoreOrderStatus.CONSOLIDATED) {
            dto.setStatus(StoreOrderStatus.APPROVED);
        }
        return dto;
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
                order.getDeliveryDate(),
                previousStatus,
                order.getStatus(),
                actor,
                actionAt,
                cancelReason,
                message);
    }
}
