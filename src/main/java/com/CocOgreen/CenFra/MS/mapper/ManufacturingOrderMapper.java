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
    @Mapping(source = "product.unit", target = "unit")
    @Mapping(source = "quantityPlanned", target = "quantity")
    ManuOrderResponse toResponse(ManufacturingOrder order);

    // Request -> Entity
    @Mapping(target = "manuOrderId", ignore = true)
    @Mapping(target = "orderCode", ignore = true) // Tự sinh
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", constant = "PLANNED") // Mặc định
    @Mapping(target = "createdBy", ignore = true) // Lấy từ Security Context
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "quantityPlanned", ignore = true)
    // Product sẽ được Service tìm theo ID và set vào sau
    ManufacturingOrder toEntity(ManuOrderRequest request);
}