package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.DeliveryDto;
import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.request.DeliveryRequest;
import com.CocOgreen.CenFra.MS.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deliveries")
@Tag(name = "Dev 3 - Delivery API", description = "Điều phối các chuyến xe giao hàng (Supply Coordinator)")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "Lấy danh sách các chuyến giao hàng", description = "xem thông tin cơ bản của các phiếu gắn kèm.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedData<DeliveryDto>>> getAllDeliveries(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(deliveryService.findAll(pageable), "Lấy danh sách thành công"));
    }

    @Operation(summary = "Lên lịch chuyến xe mới (Assign Export Notes)", description = "Tạo xe mới và gom nhóm các phiếu xuất có trạng thái READY vào xe.")
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryDto>> createDelivery(@RequestBody DeliveryRequest request) {
        DeliveryDto response = deliveryService.createDelivery(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Lên lịch xe thành công"));
    }

    @Operation(summary = "Xe xuất phát (IN_TRANSIT)", description = "Đánh dấu chuyến xe đã chạy. Tất cả phiếu xuất kho gắn kèm sẽ đổi sang SHIPPING.")
    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<DeliveryDto>> startDelivery(@PathVariable Integer id) {
        DeliveryDto response = deliveryService.startDelivery(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Xe đã xuất phát thành công."));
    }

    @Operation(summary = "Xe đến Store (COMPLETED)", description = "Đánh dấu xe đã tới nơi. Cập nhật phiếu xuất kho thành SHIPPED.")
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<DeliveryDto>> completeDelivery(@PathVariable Integer id) {
        DeliveryDto response = deliveryService.completeDelivery(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Chuyến giao hàng hoàn thành."));
    }

    @Operation(summary = "Tìm kiếm phiếu suất kho sẵn sàng", description = "Giúp tạo các delivery order")
    @GetMapping("/ready-note")
    public ResponseEntity<ApiResponse<List<ExportNoteDto>>> getReadyNote(){
        List<ExportNoteDto> list = deliveryService.getReadyExportNote();
        return ResponseEntity.ok(ApiResponse.success(list,"Lấy danh sách ExportNote đủ điều kiện thành công"));
    }
}