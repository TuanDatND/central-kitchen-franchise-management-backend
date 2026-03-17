package com.CocOgreen.CenFra.MS.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.dto.request.ManualExportRequest;
import com.CocOgreen.CenFra.MS.dto.response.ExportPreviewResponse;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.service.ExportNoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export-notes")
@Tag(name = "Dev 3 - Export Note API", description = "Quản lý Phiếu xuất kho và Giao hàng (Outbound)")
public class ExportNoteController {
    private final ExportNoteService exportNoteService;

    @Operation(summary = "Lấy danh sách tất cả Phiếu xuất kho", description = "Trả về danh sách tất cả các phiếu xuất hiện có trong hệ thống.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedData<ExportNoteDto>>> getAllExportNotes(
            @PageableDefault(size = 10) Pageable pageable) {
        PagedData<ExportNoteDto> response = exportNoteService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response,"Trả về danh sách tất cả các phiếu xuất thành công"));
    }

    @Operation(summary = "Lấy danh sách Phiếu xuất kho theo code", description = "Trả về danh sách các phiếu xuất hiện có trong hệ thống theo code.")
    @GetMapping("/findByCode/{code:.+}")
    public ResponseEntity<ApiResponse<PagedData<ExportNoteDto>>> getExportNotesByCode(
            @PathVariable("code") String code, 
            @PageableDefault(size = 10) Pageable pageable) {

        if (code == null || code.trim().isEmpty() || code.equals("{code}")) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Vui lòng nhập mã code hợp lệ (không để trống).", "INVALID_CODE"));
        }

        PagedData<ExportNoteDto> response = exportNoteService.findByExportCode(code, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm thành công"));
    }

    @Operation(summary = "Lấy danh sách các StoreOrder đủ điều kiện xuất kho", description = "Tìm các StoreOrder có trạng thái APPROVED và tổng số lượng hàng trong các lô (Batch) có sẵn đủ để đáp ứng.")
    @GetMapping("/ready-orders")
    public ResponseEntity<ApiResponse<List<StoreOrderDTO>>> getReadyStoreOrders() {
        List<StoreOrderDTO> response = exportNoteService.getReadyStoreOrders();
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy danh sách StoreOrder đủ điều kiện thành công"));
    }

    @Operation(summary = "Tạo Phiếu xuất kho thủ công (Manual)(!CHÚ Ý CÁI NÀY CHỈ LÀ BẢN BETA KHI NÀO LÀM XONG HẾT, RẢNH MỚI LÀM CÁI NÀY)", description = "Dành cho nhân viên kho tự chọn lô hàng cụ thể để xuất kho dựa trên Store Order.")
    @PostMapping("/createManualNote")
    public ResponseEntity<ApiResponse<ExportNoteDto>> createManualNote(@RequestBody ManualExportRequest request) {
        ExportNoteDto response = exportNoteService.createExportFromManualBatches(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Xuất Kho Thành Công"));
    }

    @Operation(
        summary = "Xem trước kế hoạch xuất kho FEFO (Preview)",
        description = "Hệ thống mô phỏng thuật toán FEFO và trả về danh sách lô hàng dự kiến sẽ được lấy "
                    + "cho từng sản phẩm trong đơn hàng. KHÔNG lưu dữ liệu. "
                    + "Dùng để xem trước trước khi gọi /createAutoNote."
    )
    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<List<ExportPreviewResponse>>> previewAutoNote(
            @RequestBody List<Integer> storeOrderIds) {
        List<ExportPreviewResponse> response = exportNoteService.previewExportFromOrder(storeOrderIds);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Xem trước kế hoạch xuất kho thành công. Vui lòng kiểm tra và xác nhận."));
    }

    @Operation(summary = "Tạo Phiếu xuất kho tự động (Auto FEFO) cho nhiều đơn", description = "Hệ thống tự động quét và xuất kho theo FEFO dựa trên danh sách các Store Order ID.")
    @PostMapping("/createAutoNote")
    public ResponseEntity<ApiResponse<List<ExportNoteDto>>> createAutoNote(@RequestBody List<Integer> storeOrderIds) {
        List<ExportNoteDto> response = exportNoteService.createExportFromOrder(storeOrderIds);
        return ResponseEntity.ok(ApiResponse.success(response, "Xuất Kho Tự Động Thành Công Cho Các Đơn Hàng"));
    }

    @Operation(summary = "Lấy thông tin chi tiết Phiếu xuất kho", description = "Tìm kiếm và trả về chi tiết của phần tử phiếu xuất theo ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExportNoteDto>> getExportNotesById(@PathVariable Integer id) {
        ExportNoteDto response = exportNoteService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm thành công"));
    }

    @Operation(summary = "Xác nhận giao hàng (SHIPPED)", description = "Đánh dấu Phiếu xuất đã được giao cho Franchise Store.")
    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipExportNote(@PathVariable Integer id) {
        exportNoteService.updateStatusExportNote(id, ExportStatus.SHIPPED);
        return ResponseEntity.ok("Xuất kho thành công. Hàng đang được giao.");
    }

    @Operation(summary = "Hủy Phiếu xuất kho", description = "Chuyển trạng thái Phiếu xuất sang CANCEL (Chỉ áp dụng khi phiếu chưa giao).")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelExportNote(@PathVariable Integer id) {
        exportNoteService.deleteNote(id);
        return ResponseEntity.ok("Huỷ phiếu xuất kho thành công.");
    }
}
