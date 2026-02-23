package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.ExportNoteRequestDto;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExportNoteMapper {

    @Mapping(target = "storeOrderId", source = "storeOrder.orderId")
    @Mapping(target = "storeName", source = "storeOrder.store.storeName")
    @Mapping(target = "items", source = "items")
    ExportNoteDto toDto(ExportNote entity);

    // Ánh xạ từ Request DTO sang Entity (thường dùng khi tạo mới)
    @Mapping(target = "exportId", ignore = true)
    @Mapping(target = "exportCode", ignore = true) // Sẽ gen tự động trong Service
    @Mapping(target = "status", constant = "READY")
    ExportNote toEntity(ExportNoteRequestDto request);
}
