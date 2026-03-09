package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.ExportItemDto;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.mapper.ExportItemMapper;
import com.CocOgreen.CenFra.MS.repository.ExportItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExportItemService {
    private final ExportItemRepository exportItemRepository;
    private  final ExportItemMapper exportItemMapper;

    public Page<ExportItemDto> findAll(Pageable pageable) {
        Page<ExportItem> exportItems = exportItemRepository.findAll(pageable);
        return exportItems.map(exportItemMapper::toDto);
    }
}
