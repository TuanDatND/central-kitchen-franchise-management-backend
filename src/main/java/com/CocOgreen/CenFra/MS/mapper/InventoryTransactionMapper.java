package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.InventoryTransactionDto;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
//    @Mapping(target = "productName", source = "productBatch.product.productName")
//    @Mapping(target = "batchCode", source = "productBatch.batchCode")
//    @Mapping(target = "transactionType", source = "transactionType")
//        // Lưu ý: Nếu muốn lấy tên người tạo, bạn cần join qua phiếu PN hoặc PX tương ứng
//    InventoryTransactionDto toDto(InventoryTransaction entity);
}
