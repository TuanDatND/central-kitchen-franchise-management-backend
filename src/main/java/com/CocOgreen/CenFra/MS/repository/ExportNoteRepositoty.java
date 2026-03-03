package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ExportNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExportNoteRepositoty extends JpaRepository<ExportNote, Integer> {
    Optional<ExportNote> findByExportCode(String exportCode);
    List<ExportNote> findByStatus(String status);
}
