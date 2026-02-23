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
<<<<<<< feature/Login-Authorize
=======
    @Mapping(target = "details", source = "orderDetails")
>>>>>>> main
    StoreOrderDTO toDTO(StoreOrder storeOrder);
}
