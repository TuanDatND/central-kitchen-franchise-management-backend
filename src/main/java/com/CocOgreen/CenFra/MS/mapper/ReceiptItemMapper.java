package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.response.InventoryReceiptResponse;
import com.CocOgreen.CenFra.MS.entity.ReceiptItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptItemMapper {

    // Map từ Entity con -> DTO con (Nested Class)
    @Mapping(source = "productBatch.batchId", target = "batchId")
    @Mapping(source = "productBatch.batchCode", target = "batchCode")
    InventoryReceiptResponse.ReceiptItemResponse toResponse(ReceiptItem item);
}