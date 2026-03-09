package com.CocOgreen.CenFra.MS.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.service.InventoryTransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory-transactions")
@PreAuthorize("hasRole('MANAGER')") 
@Tag(name = "Dev 3 - Inventory Transaction API", description = "Lịch sử biến động hàng hóa (Sổ cái kho - Immutable Log)")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @Operation(summary = "Xem Sổ Cái Kho (Transaction Log)", description = "Lấy danh sách lịch sử biến động có phân trang.")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getHistory(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(
                inventoryTransactionService.findAll(pageable), 
                "Lấy danh sách lịch sử biến động thành công"
        ));
    }

    @Operation(summary = "Tìm Kiếm Sổ Cái Kho (Transaction Log)", description = "Lấy lịch sử biến động Nhập / Xuất / Hủy của toàn bộ lô hàng theo mã tham chiếu.")
    @GetMapping("/getHistoryByCode/{referenceCode:.+}")
    public ResponseEntity<ApiResponse<?>> getHistoryByCode(
            @PathVariable String referenceCode,  
            @PageableDefault(size = 10) Pageable pageable) {
            
        return ResponseEntity.ok(ApiResponse.success(
                inventoryTransactionService.findByReferenceCode(referenceCode, pageable), 
                "Tìm kiếm thành công"
        ));
    }
}
