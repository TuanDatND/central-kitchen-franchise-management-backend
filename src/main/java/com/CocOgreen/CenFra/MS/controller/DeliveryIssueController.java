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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery-issues")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dev 1 - Delivery Issue Management", description = "APIs xử lý issue khi store từ chối nhận hàng và coordinator review để giao bù hoặc giao lại.")
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

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('SUPPLY_COORDINATOR','MANAGER')")
    @Operation(summary = "Review delivery issue", description = "Coordinator review issue: approve để tạo đơn giao bù mới, hoặc reject để giao lại trên đơn cũ.")
    public ResponseEntity<ApiResponse<DeliveryIssueResponse>> review(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewDeliveryIssueRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                deliveryIssueService.reviewIssue(id, request),
                "Review delivery issue thành công"));
    }
}
