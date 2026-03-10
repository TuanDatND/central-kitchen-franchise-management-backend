package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.PagedData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.response.NearExpiryBatchResponse;
import com.CocOgreen.CenFra.MS.dto.response.StockSummaryResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopProductResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopStoreResponse;
import com.CocOgreen.CenFra.MS.service.InventoryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory-reports")
@RequiredArgsConstructor
@Tag(name = "Dev 3 - Inventory Report API", description = "Báo cáo Tồn Kho & Cảnh báo hạn sử dụng lô hàng & Thống kê")
public class InventoryReportController {

    private final InventoryReportService inventoryReportService;

    // Danh sách các lô hàng sắp hết hạn
    @Operation(summary = "Báo cáo lô hàng sắp hết hạn", description = "Danh sách lô sắp đến hạn sử dụng cần ưu tiên (FEFO). Quyền: MANAGER, SUPPLY_COORDINATOR")
    @PreAuthorize("hasAnyRole('MANAGER', 'SUPPLY_COORDINATOR')")
    @GetMapping("/near-expiry")
    public ResponseEntity<ApiResponse<PagedData<NearExpiryBatchResponse>>> getNearExpiry(
            @RequestParam(defaultValue = "14") int daysThreshold) {
        PagedData<NearExpiryBatchResponse> data = inventoryReportService.getNearExpiryBatches(daysThreshold);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách lô hàng sắp hết hạn thành công"));
    }

    // Báo cáo tổng tồn kho
    @Operation(summary = "Báo cáo tổng tồn kho", description = "Tính tổng số lượng tồn kho theo sản phẩm. Quyền: MANAGER, SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF")
    @PreAuthorize("hasAnyRole('MANAGER', 'SUPPLY_COORDINATOR', 'CENTRAL_KITCHEN_STAFF')")
    @GetMapping("/stock-summary")
    public ResponseEntity<ApiResponse<PagedData<StockSummaryResponse>>> getStockSummary() {
        PagedData<StockSummaryResponse> data = inventoryReportService.getStockSummary();
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy báo cáo tổng tồn kho thành công"));
    }

    // Top món tiêu thụ mạnh nhất
    @Operation(summary = "Top món tiêu thụ mạnh nhất", description = "Thống kê sản phẩm được xuất kho nhiều nhất. Quyền: MANAGER, ADMIN")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("/top-consumed")
    public ResponseEntity<ApiResponse<PagedData<TopProductResponse>>> getTopConsumedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        PagedData<TopProductResponse> data = inventoryReportService.getTopConsumedProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách top món tiêu thụ thành công"));
    }

    // Top cửa hàng nhập lớn nhất
    @Operation(summary = "Top cửa hàng nhập lớn nhất", description = "Thống kê cửa hàng đã nhập nhiều hàng nhất. Quyền: MANAGER, ADMIN")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @GetMapping("/top-importing-stores")
    public ResponseEntity<ApiResponse<PagedData<TopStoreResponse>>> getTopImportingStores(
            @RequestParam(defaultValue = "10") int limit) {
        PagedData<TopStoreResponse> data = inventoryReportService.getTopImportingStores(limit);
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách top cửa hàng nhập lớn nhất thành công"));
    }
}
