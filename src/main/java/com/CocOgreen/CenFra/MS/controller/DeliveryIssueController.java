package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.DeliveryIssueResponse;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.ReviewDeliveryIssueRequest;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import com.CocOgreen.CenFra.MS.service.DeliveryIssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/delivery-issues")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dev 1 - Delivery Issue Management", description = "APIs xử lý issue khi store từ chối nhận hàng và coordinator review để giao bù hoặc chốt đơn cũ.")
public class DeliveryIssueController {

    private final DeliveryIssueService deliveryIssueService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Lấy danh sách delivery issue", description = "SUPPLY_COORDINATOR hoặc MANAGER xem danh sách issue giao hàng để review.")
    public ResponseEntity<ApiResponse<PagedData<DeliveryIssueResponse>>> list(
            @RequestParam(required = false) DeliveryIssueStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.listIssues(status, page, size),
                "Lấy danh sách delivery issue thành công"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Lấy chi tiết delivery issue", description = "SUPPLY_COORDINATOR hoặc MANAGER xem chi tiết một issue giao hàng.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> detail(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.getIssueDetail(id),
                "Lấy chi tiết delivery issue thành công"));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Tải ảnh minh chứng cho delivery issue", description = "Store hoặc coordinator tải thêm ảnh minh chứng cho issue đang chờ xử lý. Chỉ cho phép khi issue còn PENDING_REVIEW.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> uploadImages(
            @PathVariable Integer id,
            @RequestParam("images") java.util.List<MultipartFile> images) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.uploadIssueImages(id, images),
                "Tải ảnh minh chứng thành công"));
    }

    @DeleteMapping("/{issueId}/images")
    @PreAuthorize("hasAnyRole('FRANCHISE_STORE_STAFF','SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Xóa ảnh minh chứng của delivery issue", description = "Store hoặc coordinator xóa ảnh minh chứng khỏi issue đang chờ xử lý.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> deleteImage(
            @PathVariable Integer issueId,
            @RequestParam String imageUrl) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.deleteIssueImage(issueId, imageUrl),
                "Xóa ảnh minh chứng thành công"));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Review delivery issue", description = "Coordinator review issue: approve để tạo đơn giao bù mới, hoặc reject để chốt đơn cũ là đã nhận/hoàn tất. Reject không được truyền newDeliveryDate.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> review(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewDeliveryIssueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.reviewIssue(id, request),
                "Review delivery issue thành công"));
    }
}
