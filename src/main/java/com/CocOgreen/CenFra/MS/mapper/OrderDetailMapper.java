package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.OrderDetailDTO;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

<<<<<<< feature/Login-Authorize
    @Mapping(source = "product.productId", target = "productId")
    OrderDetailDTO toDto(OrderDetail entity);

    @Mapping(target = "storeOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderDetail toEntity(OrderDetailDTO dto);
=======
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "unit", source = "product.unit")
    OrderDetailDTO toDTO(OrderDetail orderDetail);
>>>>>>> main
}
