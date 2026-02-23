package com.CocOgreen.CenFra.MS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory-reports")
public class InventoryReportController {
    // Danh sách các lô hàng sắp hết hạn (Cần ưu tiên xuất trước)
    @GetMapping("/near-expiry")
    public ResponseEntity<?> getNearExpiry() {
        return ResponseEntity.ok(List.of(
                Map.of("batchCode", "LOT-MILK-11", "product", "Sữa tươi", "expiryDate", "2024-02-15", "stock", 5),
                Map.of("batchCode", "LOT-EGG-02", "product", "Trứng gà", "expiryDate", "2024-02-12", "stock", 50)
        ));
    }

    // Báo cáo tổng tồn kho theo từng sản phẩm (gộp tất cả các lô)
    @GetMapping("/stock-summary")
    public ResponseEntity<?> getStockSummary() {
        return ResponseEntity.ok(List.of(
                Map.of("product", "Thịt Bò Mỹ", "totalStock", 150, "unit", "kg"),
                Map.of("product", "Sốt BBQ", "totalStock", 45, "unit", "hũ")
        ));
    }
}
