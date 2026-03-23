package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.CancelOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidateOrdersRequest;
import com.CocOgreen.CenFra.MS.dto.ConsolidatedOrderResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.OrderActionResponseDTO;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.DeliveryIssueResponse;
import com.CocOgreen.CenFra.MS.dto.RejectDeliveryRequest;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreOrderRequest;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.service.DeliveryIssueService;
import com.CocOgreen.CenFra.MS.service.StoreOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dev 1 - Store Order Management", description = "APIs quản lý luồng xin hàng của cửa hàng: tạo đơn, theo dõi đơn, duyệt/hủy và gom đơn.")
public class StoreOrderController {
    private final StoreOrderService service;
    private final DeliveryIssueService deliveryIssueService;

    @PostMapping
    @PreAuthorize("hasRole('FRANCHISE_STORE_STAFF')")
    @Operation(summary = "Tạo đơn yêu cầu cấp hàng", description = "FRANCHISE_STORE_STAFF tạo đơn xin hàng cho cửa hàng mình.")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> create(@Valid @RequestBody CreateStoreOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.createOrder(request), "Tạo đơn thành công"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Lấy danh sách đơn", description = "Nhân viên cửa hàng xem đơn của cửa hàng mình. SUPPLY_COORDINATOR và MANAGER xem tổng hợp toàn hệ thống.")
    public ResponseEntity<ApiResponse<PagedData<StoreOrderDTO>>> list(
            @RequestParam(required = false) StoreOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        Page<StoreOrderDTO> orders = service.listOrders(status, normalizedPage, normalizedSize);
        PagedData<StoreOrderDTO> data = new PagedData<>(
                orders.getContent(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isFirst(),
                orders.isLast());
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách đơn thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Lấy chi tiết đơn", description = "Xem chi tiết một đơn yêu cầu cấp hàng theo ID.")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getOrderDetail(id), "Lấy chi tiết đơn thành công"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('FRANCHISE_STORE_STAFF')")
    @Operation(summary = "Chỉnh sửa đơn", description = "FRANCHISE_STORE_STAFF được cập nhật đơn của chính cửa hàng mình khi đơn vẫn đang chờ duyệt.")
    public ResponseEntity<ApiResponse<StoreOrderDTO>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.updateOrder(id, request), "Cập nhật đơn thành công"));
    }

    @GetMapping("/dashboard/top-stores")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Thống kê top cửa hàng", description = "Thống kê cửa hàng có số lượng đơn nhiều nhất để phục vụ điều phối.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topStores(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity
                .ok(ApiResponse.success(service.getTopStoresByOrderCount(limit), "Lấy top cửa hàng thành công"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Duyệt đơn", description = "SUPPLY_COORDINATOR hoặc MANAGER duyệt đơn từ trạng thái PENDING sang APPROVED.")
    public ResponseEntity<ApiResponse<OrderActionResponseDTO>> approve(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.approveOrder(id), "Duyệt đơn thành công"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('FRANCHISE_STORE_STAFF')")
    @Operation(summary = "Hủy đơn", description = "FRANCHISE_STORE_STAFF chỉ được hủy đơn PENDING của chính cửa hàng mình.")
    public ResponseEntity<ApiResponse<OrderActionResponseDTO>> cancel(
            @PathVariable Integer id,
            @Valid @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.cancelOrder(id, request), "Hủy đơn thành công"));
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasRole('FRANCHISE_STORE_STAFF')")
    @Operation(summary = "Xác nhận nhận hàng", description = "FRANCHISE_STORE_STAFF xác nhận đã nhận đơn của chính cửa hàng mình để chuyển trạng thái từ IN_TRANSIT sang DONE.")
    public ResponseEntity<ApiResponse<OrderActionResponseDTO>> receive(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.receiveOrder(id), "Xác nhận nhận hàng thành công"));
    }

    @PostMapping(value = "/{id}/reject-delivery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('FRANCHISE_STORE_STAFF')")
    @Operation(summary = "Từ chối nhận hàng", description = "FRANCHISE_STORE_STAFF gửi payload JSON gồm lý do + ghi chú và ảnh minh chứng (nếu có) khi từ chối nhận đơn đang giao. Hệ thống tạo delivery issue để coordinator review.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> rejectDelivery(
            @PathVariable Integer id,
            @Valid @RequestPart("payload") RejectDeliveryRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.reportIssue(id, request, images),
                "Tạo delivery issue thành công"));
    }

    @PostMapping("/consolidate/auto")
    @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
    @Operation(summary = "Gom đơn tự động", description = "SUPPLY_COORDINATOR bấm một nút để hệ thống tự lấy tất cả đơn APPROVED và gom theo từng sản phẩm. Sau khi gom thành công, các đơn tham gia sẽ chuyển sang CONSOLIDATED.")
    public ResponseEntity<ApiResponse<ConsolidatedOrderResponse>> consolidateAutomatically() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        service.consolidateOrdersAutomatically(),
                        "Gom đơn tự động thành công"
                )
        );
    }

    @PostMapping("/consolidate/manual")
    @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
    @Operation(summary = "Gom đơn thủ công", description = "SUPPLY_COORDINATOR chọn danh sách orderIds, hệ thống tự nhóm các đơn đó theo sản phẩm rồi chuyển các đơn tham gia sang CONSOLIDATED.")
    public ResponseEntity<ApiResponse<ConsolidatedOrderResponse>> consolidateManually(
            @Valid @RequestBody ConsolidateOrdersRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        service.consolidateOrdersManually(request.getOrderIds()),
                        "Gom đơn thủ công thành công"
                )
        );
    }

    @PostMapping("/consolidate/cancel")
    @PreAuthorize("hasRole('SUPPLY_COORDINATOR')")
    @Operation(summary = "Hủy gom đơn", description = "SUPPLY_COORDINATOR hủy gom các đơn đang ở trạng thái CONSOLIDATED để đưa chúng về APPROVED và gom lại thủ công.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelConsolidation(
            @Valid @RequestBody ConsolidateOrdersRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        service.cancelConsolidation(request.getOrderIds()),
                        "Hủy gom đơn thành công"
                )
        );
    }
}
