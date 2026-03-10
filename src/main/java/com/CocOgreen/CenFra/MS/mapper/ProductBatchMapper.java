package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.response.ProductBatchResponse;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductBatchMapper {

    // Entity -> Response
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unit.unitName", target = "unitName")
    ProductBatchResponse toResponse(ProductBatch batch);
}