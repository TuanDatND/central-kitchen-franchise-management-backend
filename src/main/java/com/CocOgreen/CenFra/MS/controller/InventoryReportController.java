package com.CocOgreen.CenFra.MS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/inventory-reports")
@Tag(name = "Dev 3 - Inventory Report API", description = "Báo cáo Tồn Kho & Cảnh báo hạn sử dụng lô hàng")
public class InventoryReportController {
    // Danh sách các lô hàng sắp hết hạn (Cần ưu tiên xuất trước)
    @Operation(summary = "Báo cáo lô hàng sắp hết hạn", description = "Danh sách các lô hàng sắp đến hạn sử dụng cần ưu tiên luân chuyển hoặc tiêu hủy (Hỗ trợ chiến lược FEFO).")
    @GetMapping("/near-expiry")
    public ResponseEntity<?> getNearExpiry() {
        return ResponseEntity.ok(List.of(
                Map.of("batchCode", "LOT-MILK-11", "product", "Sữa tươi", "expiryDate", "2024-02-15", "stock", 5),
                Map.of("batchCode", "LOT-EGG-02", "product", "Trứng gà", "expiryDate", "2024-02-12", "stock", 50)
        ));
    }

    // Báo cáo tổng tồn kho theo từng sản phẩm (gộp tất cả các lô)
    @Operation(summary = "Báo cáo tổng tồn kho", description = "Tính tổng số lượng tồn kho của từng sản phẩm hiện có trong kho trung tâm (gộp từ tất cả các lô).")
    @GetMapping("/stock-summary")
    public ResponseEntity<?> getStockSummary() {
        return ResponseEntity.ok(List.of(
                Map.of("product", "Thịt Bò Mỹ", "totalStock", 150, "unit", "kg"),
                Map.of("product", "Sốt BBQ", "totalStock", 45, "unit", "hũ")
        ));
    }
}
