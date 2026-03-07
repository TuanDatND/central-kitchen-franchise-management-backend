package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.request.UnitRequest;
import com.CocOgreen.CenFra.MS.dto.response.UnitResponse;
import com.CocOgreen.CenFra.MS.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/units")
@RequiredArgsConstructor
@Tag(name = "Unit API", description = "Các API quản lý Đơn vị tính (Master Data)")
public class UnitController {

    private final UnitService unitService;

    @Operation(summary = "Lấy toàn bộ danh sách đơn vị tính")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<UnitResponse>>> getAllUnits() {
        List<UnitResponse> responses = unitService.getAllUnits();
        return ResponseEntity.ok(ApiResponse.success(responses, "Lấy danh sách đơn vị thành công"));
    }

    @Operation(summary = "Lấy chi tiết đơn vị tính theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UnitResponse>> getUnitById(@PathVariable Integer id) {
        UnitResponse response = unitService.getUnitById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin đơn vị thành công"));
    }

    @Operation(summary = "Thêm mới đơn vị tính (Chỉ MANAGER)")
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<UnitResponse>> createUnit(@RequestBody @Valid UnitRequest request) {
        UnitResponse response = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Tạo đơn vị thành công"));
    }

    @Operation(summary = "Cập nhật đơn vị tính (Chỉ MANAGER)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<UnitResponse>> updateUnit(
            @PathVariable Integer id,
            @RequestBody @Valid UnitRequest request) {
        UnitResponse response = unitService.updateUnit(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật đơn vị thành công"));
    }

    @Operation(summary = "Xóa mềm đơn vị tính (Chuyển trạng thái sang INACTIVE, chỉ MANAGER)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteUnit(@PathVariable Integer id) {
        unitService.deleteUnit(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đơn vị thành công"));
    }
}
