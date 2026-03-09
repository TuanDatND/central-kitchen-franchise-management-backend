package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.InventoryTransactionDto;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    @Mapping(target = "productName", source = "productBatch.product.productName")
    @Mapping(target = "batchCode", source = "productBatch.batchCode")
    @Mapping(target = "transactionType", source = "transactionType")
    @Mapping(target = "createdByFullName", source = "user.fullName")
    InventoryTransactionDto toDto(InventoryTransaction entity);
}
