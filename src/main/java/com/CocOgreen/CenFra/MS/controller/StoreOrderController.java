package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidateOrdersRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidatedOrderResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.OrderActionResponseDTO;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.service.StoreOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StoreOrderController {
    private final StoreOrderService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> create(@Valid @RequestBody CreateStoreOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.createOrder(request), "Tạo đơn thành công"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER','CENTRAL_KITCHEN_STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<PagedData<StoreOrderDTO>>> list(
            @RequestParam(required = false) StoreOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        Page<StoreOrderDTO> orders = service.listOrders(status, normalizedPage, normalizedSize);
        PagedData<StoreOrderDTO> data = new PagedData<>(
                orders.getContent(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isFirst(),
                orders.isLast());
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách đơn thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER','CENTRAL_KITCHEN_STAFF','ADMIN')")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getOrderDetail(id), "Lấy chi tiết đơn thành công"));
    }

    @GetMapping("/dashboard/top-stores")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topStores(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity
                .ok(ApiResponse.success(service.getTopStoresByOrderCount(limit), "Lấy top cửa hàng thành công"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<OrderActionResponseDTO>> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.approveOrder(id), "Duyệt đơn thành công"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<OrderActionResponseDTO>> cancel(
            @PathVariable Integer id,
            @Valid @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.cancelOrder(id, request), "Hủy đơn thành công"));
    }

    @PostMapping("/consolidate")
    @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
    public ResponseEntity<ApiResponse<List<ConsolidatedOrderResponse>>> consolidate(
            @Valid @RequestBody ConsolidateOrdersRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(service.consolidateOrders(request.getOrderIds()), "Gom đơn thành công"));
    }

    @PostMapping("/consolidate")
    @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
    public ResponseEntity<ConsolidatedOrderResponse> consolidate(
            @Valid @RequestBody ConsolidateOrdersRequest request) {
        return ResponseEntity.ok(service.consolidateOrders(request.getOrderIds()));
    }
}
