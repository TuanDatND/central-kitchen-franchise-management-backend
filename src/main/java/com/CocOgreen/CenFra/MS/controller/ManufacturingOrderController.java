package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.request.ManuOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/manufacturing-orders")
@Tag(name = "Production Management", description = "APIs quản lý lệnh sản xuất (Quang)")
public class ManufacturingOrderController {

    @PostMapping
    @Operation(summary = "Tạo lệnh sản xuất mới", description = "Coordinator gửi yêu cầu nấu")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody ManuOrderRequest request) {
        // Logic giả: Trả về thông báo thành công
        return ResponseEntity.ok(Map.of(
                "message", "Tạo lệnh sản xuất thành công!",
                "orderCode", "MO-20240210-001",
                "status", "PLANNED",
                "quantity", request.getQuantity()
        ));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái lệnh", description = "Bếp trưởng cập nhật: PLANNED -> COOKING -> COMPLETED")
    public ResponseEntity<String> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        // Logic giả
        return ResponseEntity.ok("Đã cập nhật lệnh ID " + id + " sang trạng thái: " + status);
    }
}