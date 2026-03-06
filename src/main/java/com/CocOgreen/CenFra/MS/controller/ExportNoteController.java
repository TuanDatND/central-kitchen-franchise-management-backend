package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.ExportNoteRequestDto;
import com.CocOgreen.CenFra.MS.dto.request.ManualExportRequest;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.service.ExportNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/export-notes")
public class ExportNoteController {
    private final ExportNoteService exportNoteService;

    @GetMapping
    public ResponseEntity<?> getAllExportNotes() {
        return ResponseEntity.ok(List.of(exportNoteService.findAll()));
    }

    @PostMapping("/createManualNote")
    public ResponseEntity<ApiResponse<ExportNoteDto>> createManualNote(@RequestBody ManualExportRequest request) {
        ExportNoteDto response = exportNoteService.createExportFromManualBatches(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Xuất Kho Thành Công"));
    }

    @PostMapping("/createAutoNote/{storeOderId}")
    public ResponseEntity<ApiResponse<ExportNoteDto>> createAutoNote(@PathVariable Integer storeOderId) {
        ExportNoteDto response = exportNoteService.createExportFromOrder(storeOderId);
        return ResponseEntity.ok(ApiResponse.success(response, "Xuất Kho Thành Công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getExportNotesById(@PathVariable Integer id) {
        ExportNoteDto response = exportNoteService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Tìm kiếm thành công"));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipExportNote(@PathVariable Integer id) {
        exportNoteService.updateStatusExportNote(id, ExportStatus.SHIPPED);
        return ResponseEntity.ok("Xuất kho thành công. Hàng đang được giao.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelExportNote(@PathVariable Integer id) {
        exportNoteService.deleteNote(id);
        return ResponseEntity.ok("Huỷ phiếu xuất kho thành công.");
    }
}
