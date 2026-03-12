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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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

    // 2. API Lấy danh sách lịch sử phiếu nhập kho
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CENTRAL_KITCHEN_STAFF')")
    @Operation(summary = "Lấy danh sách lịch sử phiếu nhập kho", description = "Sắp xếp theo thời gian mới nhất (DESC).")
    public ResponseEntity<ApiResponse<List<InventoryReceiptResponse>>> getAllReceipts() {
        List<InventoryReceiptResponse> responseList = inventoryReceiptService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success(responseList, "Fetched all inventory receipts successfully"));
    }

    // 3. API Lấy chi tiết phiếu nhập kho theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CENTRAL_KITCHEN_STAFF')")
    @Operation(summary = "Lấy chi tiết phiếu nhập kho", description = "Lấy thông tin một phiếu nhập kho cụ thể theo ID.")
    public ResponseEntity<ApiResponse<InventoryReceiptResponse>> getReceiptById(@PathVariable Integer id) {
        InventoryReceiptResponse response = inventoryReceiptService.getReceiptById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Fetched inventory receipt successfully"));
    }
}
