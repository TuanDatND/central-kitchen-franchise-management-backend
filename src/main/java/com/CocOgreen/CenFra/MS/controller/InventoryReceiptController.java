package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.request.InventoryReceiptRequest;
import com.CocOgreen.CenFra.MS.dto.response.InventoryReceiptResponse;
import com.CocOgreen.CenFra.MS.service.InventoryReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý API Nhập Kho (Inbound Inventory Receipt).
 * Thuộc phạm vi của Backend Dev 2 (Master Data & Inbound).
 */
@RestController
@RequestMapping("/api/v1/inventory-receipts")
@RequiredArgsConstructor
@Tag(name = "Inbound Inventory Management", description = "APIs quản lý nhập kho (Dev 2)")
public class InventoryReceiptController {

    private final InventoryReceiptService inventoryReceiptService;

    // 1. API Xác nhận Nhập Kho
    @PostMapping
    @PreAuthorize("hasRole('CENTRAL_KITCHEN_STAFF')")
    @Operation(summary = "Tạo phiếu nhập kho xác nhận số lượng thực tế", description = "Dành riêng cho CENTRAL_KITCHEN_STAFF. Chốt số lượng nấu được và chuyển trạng thái lô hàng thành AVAILABLE.")
    public ResponseEntity<ApiResponse<InventoryReceiptResponse>> createReceipt(
            @Valid @RequestBody InventoryReceiptRequest request) {
        InventoryReceiptResponse response = inventoryReceiptService.createReceipt(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo phiếu nhập kho thành công"));
    }
}
