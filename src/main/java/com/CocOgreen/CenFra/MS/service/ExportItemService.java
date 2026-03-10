package com.CocOgreen.CenFra.MS.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.ExportItemDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.mapper.ExportItemMapper;
import com.CocOgreen.CenFra.MS.repository.ExportItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportItemService {
    private final ExportItemRepository exportItemRepository;
    private  final ExportItemMapper exportItemMapper;

    public PagedData<ExportItemDto> findAll(Integer exportId, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<ExportItem> page;
        if (exportId != null) {
            page = exportItemRepository.findByExportNote_ExportId(exportId, pageable);
        } else {
            page = exportItemRepository.findAll(pageable);
        }
        List<ExportItemDto> dtoList = page.getContent().stream().map(exportItemMapper::toDto).toList();
        return new com.CocOgreen.CenFra.MS.dto.PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
