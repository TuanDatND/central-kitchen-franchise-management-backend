package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.CreateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.dto.OrderActionResponseDTO;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StoreOrderController {
    private final StoreOrderService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('STORE_MANAGER','COORDINATOR','ADMIN')")
    public ResponseEntity<StoreOrderDTO> create(@Valid @RequestBody CreateStoreOrderRequest request) {
        return ResponseEntity.ok(service.createOrder(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STORE_MANAGER','COORDINATOR','ADMIN')")
    public ResponseEntity<Page<StoreOrderDTO>> list(
            @RequestParam(required = false) StoreOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        return ResponseEntity.ok(service.listOrders(status, normalizedPage, normalizedSize));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','COORDINATOR','ADMIN')")
    public ResponseEntity<StoreOrderDTO> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getOrderDetail(id));
    }

    @GetMapping("/dashboard/top-stores")
    @PreAuthorize("hasAnyRole('COORDINATOR','ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> topStores(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getTopStoresByOrderCount(limit));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('COORDINATOR','ADMIN')")
    public ResponseEntity<OrderActionResponseDTO> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(service.approveOrder(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('STORE_MANAGER','COORDINATOR','ADMIN')")
    public ResponseEntity<OrderActionResponseDTO> cancel(
            @PathVariable Integer id,
            @Valid @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok(service.cancelOrder(id, request));
    }
}
