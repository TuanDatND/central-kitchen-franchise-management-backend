package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.request.ManuOrderRequest;
import com.CocOgreen.CenFra.MS.dto.response.ManuOrderResponse;
import com.CocOgreen.CenFra.MS.enums.ManuOrderStatus;
import com.CocOgreen.CenFra.MS.service.ManufacturingOrderService;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý API của Lệnh Sản Xuất (Manufacturing Order).
 * Thuộc phạm vi của Backend Dev 2 (Master Data & Inbound).
 */
@RestController
@RequestMapping("/api/v1/manufacturing-orders")
@RequiredArgsConstructor
@Tag(name = "Production Management", description = "APIs quản lý lệnh sản xuất (Dev 2)")
public class ManufacturingOrderController {

        private final ManufacturingOrderService manufacturingOrderService;

        // 1. API Tạo lệnh sản xuất (Coordinator gửi yêu cầu)
        @PostMapping
        @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
        @Operation(summary = "Tạo lệnh sản xuất mới hàng loạt", description = "Dành riêng cho SUPPLY_COORDINATOR. Lên kế hoạch sản xuất dựa vào danh sách ID món và số lượng.")
        public ResponseEntity<ApiResponse<List<ManuOrderResponse>>> createOrders(
                        @Valid @RequestBody ManuOrderRequest request) {
                List<ManuOrderResponse> responseList = manufacturingOrderService.createOrders(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success(responseList, "Tạo các lệnh sản xuất thành công"));
        }

        // 2. API Lấy danh sách lệnh (Dashboard cho Bếp)
        @GetMapping
        @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR', 'CENTRAL_KITCHEN_STAFF', 'MANAGER')")
        @Operation(summary = "Lấy danh sách lệnh sản xuất", description = "Xem danh sách kế hoạch sản xuất (SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF, MANAGER)")
        public ResponseEntity<ApiResponse<List<ManuOrderResponse>>> getAllOrders() {
                List<ManuOrderResponse> responseList = manufacturingOrderService.getAllOrders();
                return ResponseEntity.ok(ApiResponse.success(responseList, "Lấy danh sách lệnh sản xuất thành công"));
        }

        // 3. API Cập nhật trạng thái (Bếp trưởng bấm nút)
        @PatchMapping("/{id}/status")
        @PreAuthorize("hasRole('CENTRAL_KITCHEN_STAFF')")
        @Operation(summary = "Cập nhật trạng thái lệnh (Tự động)", description = "Dành riêng cho CENTRAL_KITCHEN_STAFF. Backend tự động chuyển tiếp trạng thái: PLANNED -> COOKING -> COMPLETED.")
        public ResponseEntity<ApiResponse<ManuOrderResponse>> updateStatus(
                        @PathVariable Integer id) {
                ManuOrderResponse response = manufacturingOrderService.updateStatus(id);
                return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật trạng thái lệnh sản xuất thành công"));
        }
}
