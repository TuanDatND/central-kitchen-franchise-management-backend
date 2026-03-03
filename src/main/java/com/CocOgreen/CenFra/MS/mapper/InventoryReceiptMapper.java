package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.request.InventoryReceiptRequest;
import com.CocOgreen.CenFra.MS.dto.response.InventoryReceiptResponse;
import com.CocOgreen.CenFra.MS.entity.InventoryReceipt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// uses = ReceiptItemMapper.class: Bảo MapStruct dùng mapper này để convert cái List<ReceiptItem>
@Mapper(componentModel = "spring", uses = { ReceiptItemMapper.class })
public interface InventoryReceiptMapper {

    // 1. Entity -> Response
    @Mapping(source = "createdBy.fullName", target = "createdBy")
    @Mapping(source = "receiptItems", target = "items")
    // MapStruct tự động map List<ReceiptItem> -> List<ReceiptItemResponse> nhờ
    // 'uses'
    InventoryReceiptResponse toResponse(InventoryReceipt receipt);

    // 2. Request -> Entity (Chỉ map vỏ phiếu nhập)
    @Mapping(target = "receiptId", ignore = true)
    @Mapping(target = "receiptCode", ignore = true) // Tự sinh
    @Mapping(target = "receiptDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "status", constant = "COMPLETED")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "receiptItems", ignore = true) // List Item xử lý riêng trong Service
    InventoryReceipt toEntity(InventoryReceiptRequest request);
}