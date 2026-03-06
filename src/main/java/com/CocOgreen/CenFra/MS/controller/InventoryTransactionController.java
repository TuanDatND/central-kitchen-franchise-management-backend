package com.CocOgreen.CenFra.MS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory-transactions")
@Tag(name = "Dev 3 - Inventory Transaction API", description = "Lịch sử biến động hàng hóa (Sổ cái kho - Immutable Log)")
public class InventoryTransactionController {
    @Operation(summary = "Xem Sổ Cái Kho (Transaction Log)", description = "Lấy lịch sử mọi biến động Nhập / Xuất / Hủy của toàn bộ lô hàng.")
    @GetMapping
    public ResponseEntity<?> getHistory() {
        return ResponseEntity.ok(List.of(
                Map.of("id", 101, "type", "EXPORT", "product", "Thịt Bò Mỹ", "qty", -25, "ref", "PX-001", "time", "2024-02-10 14:30"),
                Map.of("id", 102, "type", "IMPORT", "product", "Bánh Mì", "qty", 100, "ref", "PN-052", "time", "2024-02-10 09:00"),
                Map.of("id", 103, "type", "DISPOSAL", "product", "Sữa tươi", "qty", -5, "ref", "HUY-001", "time", "2024-02-10 16:00")
        ));
    }
}
