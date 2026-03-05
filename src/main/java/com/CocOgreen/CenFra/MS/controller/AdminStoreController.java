package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminStoreResponse;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreRequest;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreActiveRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreInfoRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreManagerRequest;
import com.CocOgreen.CenFra.MS.service.AdminStoreService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStoreController {
    private final AdminStoreService adminStoreService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedData<AdminStoreResponse>>> listStores(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        Page<AdminStoreResponse> stores = adminStoreService.listStores(active, normalizedPage, normalizedSize);
        PagedData<AdminStoreResponse> data = new PagedData<>(
                stores.getContent(),
                stores.getNumber(),
                stores.getSize(),
                stores.getTotalElements(),
                stores.getTotalPages(),
                stores.isFirst(),
                stores.isLast()
        );
        return ResponseEntity.ok(ApiResponse.success(data, "Lấy danh sách cửa hàng thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> getStore(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.getStore(id), "Lấy thông tin cửa hàng thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminStoreResponse>> createStore(@Valid @RequestBody CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminStoreService.createStore(request), "Tạo cửa hàng thành công"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> updateStoreInfo(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreInfoRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.updateStoreInfo(id, request), "Cập nhật thông tin cửa hàng thành công"));
    }

    @PatchMapping("/{id}/manager")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> updateManager(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreManagerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.updateManager(id, request), "Cập nhật quản lý cửa hàng thành công"));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> updateActive(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreActiveRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.updateActive(id, request.getIsActive()), "Cập nhật trạng thái cửa hàng thành công"));
    }
}
