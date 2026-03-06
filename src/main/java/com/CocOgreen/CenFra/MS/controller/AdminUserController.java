package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminUserResponse;
import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.CreateUserRequest;
import com.CocOgreen.CenFra.MS.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Dev 1 - User Management", description = "APIs quản lý tài khoản nhân viên. Chỉ ADMIN được thêm, sửa và khóa tài khoản.")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tài khoản", description = "Lấy danh sách user, có thể lọc theo trạng thái active và storeId.")
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        return ResponseEntity.ok(adminUserService.listUsers(storeId, active, normalizedPage, normalizedSize));
    }

    @PostMapping
    @Operation(summary = "Tạo tài khoản mới", description = "ADMIN tạo tài khoản nhân viên mới. Chỉ FRANCHISE_STORE_STAFF mới bắt buộc nhập storeId.")
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.createUser(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật tài khoản", description = "ADMIN cập nhật thông tin tài khoản, role, mật khẩu, trạng thái và storeId nếu cần.")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody com.CocOgreen.CenFra.MS.dto.UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminUserService.updateUser(id, request),
                "Cập nhật thông tin người dùng thành công"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Khóa tài khoản", description = "Khóa mềm tài khoản bằng cách chuyển isActive về false.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteUser(@PathVariable Integer id) {
        adminUserService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of(), "Xóa mềm người dùng thành công"));
    }
}
