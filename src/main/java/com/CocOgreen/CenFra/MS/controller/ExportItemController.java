package com.CocOgreen.CenFra.MS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.ExportItemDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.service.ExportItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export-items")
@CrossOrigin(origins = "*")
@Tag(name = "Dev 3 - Export Item API", description = "Quản lý chi tiết các lô hàng trong một Phiếu xuất")
public class ExportItemController {

    private final ExportItemService exportItemService;

    @Operation(summary = "Lấy danh sách Item của Phiếu xuất", description = "Danh sách chi tiết các đợt bốc hàng theo lô (batch) của toàn bộ  .")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedData<ExportItemDto>>> getItems(
            @RequestParam(required = false) Integer exportId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(exportItemService.findAll(exportId, pageable), "Lấy danh sách thành công"));
    }

}
