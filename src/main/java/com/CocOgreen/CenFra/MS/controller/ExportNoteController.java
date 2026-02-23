package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.ExportNoteRequestDto;
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

    @GetMapping
    public ResponseEntity<?> getAllExportNotes(){
        return ResponseEntity.ok(List.of(
                Map.of("id", 1, "code", "PX-20240210-001", "store", "Chi nhánh Quận 1", "status", "READY"),
                Map.of("id", 2, "code", "PX-20240210-002", "store", "Chi nhánh Thủ Đức", "status", "SHIPPED")
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportNotesById(@PathVariable Integer id){
        return ResponseEntity.ok(Map.of(
                "id", id,
                "code", "PX-20240210-001",
                "items", List.of(
                        Map.of("product", "Thịt Bò Mỹ", "batch", "LOT-B001", "expiry", "2024-05-20", "qty", 15),
                        Map.of("product", "Thịt Bò Mỹ", "batch", "LOT-B002", "expiry", "2024-06-15", "qty", 10)
                )
        ));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable String id, @RequestBody ExportNoteRequestDto exportNoteRequestDto) {
        return ResponseEntity.ok(Map.of("message", "Đã xác nhận xuất kho và cập nhật tồn thực tế"));
    }
}
