package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminStoreResponse;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreRequest;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreRequest;
import com.CocOgreen.CenFra.MS.service.AdminStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dev 1 - Store Management", description = "APIs quản lý cửa hàng nhượng quyền. ADMIN thêm/sửa/ngưng hoạt động, các role đã đăng nhập được xem.")
public class AdminStoreController {
    private final AdminStoreService adminStoreService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy danh sách cửa hàng", description = "Lấy danh sách các cửa hàng nhượng quyền, có thể lọc theo trạng thái hoạt động.")
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lấy chi tiết cửa hàng", description = "Xem thông tin chi tiết của một cửa hàng theo ID.")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> getStore(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.getStore(id), "Lấy thông tin cửa hàng thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo cửa hàng mới", description = "ADMIN tạo mới một cửa hàng nhượng quyền.")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> createStore(@Valid @RequestBody CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminStoreService.createStore(request), "Tạo cửa hàng thành công"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật cửa hàng", description = "ADMIN cập nhật từng phần thông tin cửa hàng, bao gồm cả bật hoặc ngưng hoạt động qua trường isActive.")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> updateStore(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.updateStore(id, request), "Cập nhật cửa hàng thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ngưng hoạt động cửa hàng", description = "ADMIN xóa mềm cửa hàng bằng cách chuyển isActive về false.")
    public ResponseEntity<ApiResponse<AdminStoreResponse>> deleteStore(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(adminStoreService.softDeleteStore(id), "Ngưng hoạt động cửa hàng thành công"));
    }
}
