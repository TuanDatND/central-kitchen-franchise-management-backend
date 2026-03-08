package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.response.ProductBatchResponse;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.service.ProductBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý API Lô Hàng (Product Batches).
 * Thuộc phạm vi Backend Dev 2 (Master Data & Inbound).
 */
@RestController
@RequestMapping("/api/v1/product-batches")
@RequiredArgsConstructor
@Tag(name = "Production Management", description = "APIs quản lý lô hàng (Dev 2)")
public class ProductBatchController {

        private final ProductBatchService productBatchService;

        // API Lấy danh sách lô hàng theo trạng thái
        @GetMapping
        @PreAuthorize("hasAnyRole('CENTRAL_KITCHEN_STAFF', 'MANAGER', 'SUPPLY_COORDINATOR')")
        @Operation(summary = "Lấy danh sách lô hàng", description = "Lấy tất cả lô hàng hoặc lọc theo status (VD: WAITING_FOR_STOCK)")
        public ResponseEntity<ApiResponse<List<ProductBatchResponse>>> getBatches(
                        @RequestParam(required = false) BatchStatus status) {

                List<ProductBatchResponse> responseList = productBatchService.getBatchesByStatus(status);

                return ResponseEntity.ok(ApiResponse.success(responseList, "Lấy danh sách lô hàng thành công"));
        }
}