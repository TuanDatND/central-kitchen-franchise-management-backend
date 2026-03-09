package com.CocOgreen.CenFra.MS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.ExportItemDto;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.mapper.ExportItemMapper;
import com.CocOgreen.CenFra.MS.repository.ExportItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportItemService {
    private final ExportItemRepository exportItemRepository;
    private  final ExportItemMapper exportItemMapper;

    public List<ExportItemDto> findAll(Integer exportId) {
        List<ExportItem> exportItems;
        if (exportId != null) {
            exportItems = exportItemRepository.findByExportNote_ExportId(exportId);
        } else {
            exportItems = exportItemRepository.findAll();
        }
        return exportItems.stream().map(exportItemMapper::toDto).toList();
    }
}
