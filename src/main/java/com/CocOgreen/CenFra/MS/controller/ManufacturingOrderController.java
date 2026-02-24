package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.request.ManuOrderRequest;
import com.CocOgreen.CenFra.MS.dto.response.ManuOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manufacturing-orders")
@Tag(name = "Production Management", description = "APIs quản lý lệnh sản xuất (Dev 2)")
public class ManufacturingOrderController {

    // 1. API Tạo lệnh sản xuất (Coordinator gửi yêu cầu)
    @PostMapping
    @Operation(summary = "Tạo lệnh sản xuất mới", description = "Nhận ID món + Số lượng -> Trả về chi tiết lệnh kèm Mã lệnh và Tên món")
    public ResponseEntity<ManuOrderResponse> createOrder(@Valid @RequestBody ManuOrderRequest request) {

        // MOCK LOGIC: Giả lập database tìm Product theo ID
        // Frontend gửi productId = 1 -> Backend tự biết đó là "Thịt Bò"
        String mockProductName = "Sản phẩm (ID: " + request.getProductId() + ")";
        String mockUnit = "kg";

        if (request.getProductId() == 1) {
            mockProductName = "Thịt Bò Úc Sốt Vang";
            mockUnit = "kg";
        } else if (request.getProductId() == 2) {
            mockProductName = "Canh Chua Cá Lóc";
            mockUnit = "suất";
        }

        // Giả lập sinh mã lệnh ngẫu nhiên
        String mockOrderCode = "MO-" + System.currentTimeMillis();

        // Tạo Response giả trả về
        ManuOrderResponse response = new ManuOrderResponse(
                101,                            // ID tự sinh
                mockOrderCode,                  // Mã lệnh
                mockProductName,                // Tên món (Lấy từ logic mock trên)
                request.getQuantity(),          // Số lượng (Lấy từ request)
                mockUnit,                       // Đơn vị
                "PLANNED",                      // Trạng thái mặc định
                request.getStartDate(),         // Ngày bắt đầu
                "Coordinator User"              // Người tạo
        );

        return ResponseEntity.ok(response);
    }

    // 2. API Lấy danh sách lệnh (Dashboard cho Bếp)
    // Cần thêm cái này để Bếp biết hôm nay nấu gì
    @GetMapping
    @Operation(summary = "Lấy danh sách lệnh sản xuất", description = "Trả về lịch sản xuất giả định cho Bếp trưởng")
    public ResponseEntity<List<ManuOrderResponse>> getAllOrders() {

        List<ManuOrderResponse> mockList = Arrays.asList(
                new ManuOrderResponse(
                        101, "MO-20240210-001", "Gà Rán Giòn", 50, "cái",
                        "COOKING", Instant.now(), "Admin"
                ),
                new ManuOrderResponse(
                        102, "MO-20240210-002", "Salad Nga", 20, "hộp",
                        "PLANNED", Instant.now().plusSeconds(3600), "Admin"
                ),
                new ManuOrderResponse(
                        103, "MO-20240210-003", "Bò Hầm Tiêu", 100, "kg",
                        "COMPLETED", Instant.now().minusSeconds(7200), "Admin"
                )
        );

        return ResponseEntity.ok(mockList);
    }

    // 3. API Cập nhật trạng thái (Bếp trưởng bấm nút)
    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái lệnh", description = "Update status: PLANNED -> COOKING -> COMPLETED")
    public ResponseEntity<ManuOrderResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        // Mock trả về đối tượng đã update để UI cập nhật lại dòng đó
        ManuOrderResponse response = new ManuOrderResponse(
                id,
                "MO-EXISTING-CODE",
                "Món Đang Nấu",
                50,
                "kg",
                status, // Trạng thái mới frontend vừa gửi lên
                Instant.now(),
                "Chef"
        );

        return ResponseEntity.ok(response);
    }
}