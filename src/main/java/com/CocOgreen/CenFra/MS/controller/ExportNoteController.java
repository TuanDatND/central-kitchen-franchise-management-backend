package com.CocOgreen.CenFra.MS.controller;

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
    public ResponseEntity<ExportNoteDto> createManualNote(@RequestBody ManualExportRequest request) {
        return ResponseEntity.ok(exportNoteService.createExportFromManualBatches(request));
    }

    @PostMapping("/createAutoNote/{storeId}")
    public ResponseEntity<ExportNoteDto> createAutoNote(@PathVariable Integer storeId) {
        return ResponseEntity.ok(exportNoteService.createExportFromOrder(storeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportNotesById(@PathVariable Integer id) {
        return ResponseEntity.ok(exportNoteService.findById(id));
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
