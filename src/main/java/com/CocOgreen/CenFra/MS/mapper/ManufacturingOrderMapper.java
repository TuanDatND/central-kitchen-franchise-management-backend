package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.request.ManuOrderRequest;
import com.CocOgreen.CenFra.MS.dto.response.ManuOrderResponse;
import com.CocOgreen.CenFra.MS.entity.ManufacturingOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManufacturingOrderMapper {

    // Entity -> Response
    @Mapping(source = "createdBy.fullName", target = "createdBy") // Lấy tên người tạo
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unit.unitName", target = "unitName")
    @Mapping(source = "quantityPlanned", target = "quantity")
    ManuOrderResponse toResponse(ManufacturingOrder order);

}