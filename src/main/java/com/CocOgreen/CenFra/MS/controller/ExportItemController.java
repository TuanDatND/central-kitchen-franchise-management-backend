package com.CocOgreen.CenFra.MS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export-items")
@CrossOrigin(origins = "*")
@Tag(name = "Dev 3 - Export Item API", description = "Quản lý chi tiết các lô hàng trong một Phiếu xuất")
public class ExportItemController {
    // 1. Lấy tất cả các item thuộc về một Phiếu xuất cụ thể
    // Endpoint: GET /api/v1/export-items?exportId=1
    @Operation(summary = "Lấy danh sách Item của Phiếu xuất", description = "Danh sách chi tiết các đợt bốc hàng theo lô (batch) của một Phiếu xuất cụ thể.")
    @GetMapping
    public ResponseEntity<?> getItemsByExportNote(@RequestParam Integer exportId) {
        // Dữ liệu giả lập: Một món hàng (Thịt Bò) được tách làm 2 lô do logic FEFO
        List<Map<String, Object>> mockItems = List.of(
                Map.of(
                        "exportItemId", 10,
                        "exportId", exportId,
                        "productId", 1,
                        "productName", "Thịt Bò Mỹ",
                        "batchId", 501,
                        "batchCode", "LOT-BEEF-FEB-01",
                        "quantity", 12, // Số lượng lấy từ lô này
                        "unit", "kg",
                        "expiryDate", "2024-03-01",
                        "note", "Lấy tại ngăn đông A1"
                ),
                Map.of(
                        "exportItemId", 11,
                        "exportId", exportId,
                        "productId", 1,
                        "productName", "Thịt Bò Mỹ",
                        "batchId", 502,
                        "batchCode", "LOT-BEEF-FEB-02",
                        "quantity", 8, // Số lượng lấy thêm từ lô này cho đủ 20kg
                        "unit", "kg",
                        "expiryDate", "2024-03-15",
                        "note", "Lấy tại ngăn đông A2"
                ),
                Map.of(
                        "exportItemId", 12,
                        "exportId", exportId,
                        "productId", 5,
                        "productName", "Tương Ớt Hàn Quốc",
                        "batchId", 600,
                        "batchCode", "LOT-CHILI-09",
                        "quantity", 5,
                        "unit", "can",
                        "expiryDate", "2025-01-10",
                        "note", "Kệ đồ khô tầng 2"
                )
        );

        return ResponseEntity.ok(mockItems);
    }

    // 2. API cập nhật số lượng thực tế bốc được (Dùng cho nhân viên kho khi thực hiện bốc hàng)
    // Ví dụ: Hệ thống bảo lấy 12kg nhưng thực tế lô đó chỉ còn 11.5kg
    @Operation(summary = "Cập nhật số lượng bốc hàng thực tế", description = "Cập nhật lại số lượng hàng lấy thực tế từ lô so với dự kiến ban đầu (dành cho nhân viên kho).")
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updatePickedQuantity(
            @PathVariable Integer itemId,
            @RequestBody Map<String, Integer> payload) {

        Integer newQty = payload.get("quantity");
        return ResponseEntity.ok(Map.of(
                "message", "Đã cập nhật số lượng bốc hàng cho Item " + itemId,
                "updatedQuantity", newQty
        ));
    }
}
