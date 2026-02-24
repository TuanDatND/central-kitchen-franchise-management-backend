package com.CocOgreen.CenFra.MS.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product-batches")
@Tag(name = "Inventory Inbound", description = "APIs quản lý lô hàng nhập kho (Quang)")
public class ProductBatchController {

    @GetMapping
    @Operation(summary = "Xem danh sách lô hàng trong kho", description = "Dùng cho Dev 3 và Frontend check tồn kho")
    public ResponseEntity<List<Map<String, Object>>> getBatches() {
        // MOCK DATA: Giả lập các lô hàng đang có
        List<Map<String, Object>> mockBatches = Arrays.asList(
                Map.of(
                        "batchCode", "LOT-BEEF-001",
                        "productName", "Thịt Bò Úc",
                        "currentQuantity", 50,
                        "expiryDate", LocalDate.now().plusDays(3),
                        "status", "AVAILABLE"
                ),
                Map.of(
                        "batchCode", "LOT-VEG-002",
                        "productName", "Rau Cải",
                        "currentQuantity", 20,
                        "expiryDate", LocalDate.now().minusDays(1), // Đã hết hạn
                        "status", "EXPIRED"
                )
        );

        return ResponseEntity.ok(mockBatches);
    }
}