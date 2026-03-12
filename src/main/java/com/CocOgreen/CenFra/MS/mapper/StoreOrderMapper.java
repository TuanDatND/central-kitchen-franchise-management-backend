package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = OrderDetailMapper.class
)
public interface StoreOrderMapper {

    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "storeName", source = "store.storeName")
    @Mapping(target = "details", source = "orderDetails")
    StoreOrderDTO toDTO(StoreOrder storeOrder);

    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "storeName", source = "store.storeName")
    @Mapping(target = "details", expression = "java(java.util.Collections.emptyList())")
    StoreOrderDTO toSummaryDTO(StoreOrder storeOrder);
}
