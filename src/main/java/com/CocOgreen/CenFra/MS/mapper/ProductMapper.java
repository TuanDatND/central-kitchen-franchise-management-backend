package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.request.ProductRequest;
import com.CocOgreen.CenFra.MS.dto.response.ProductResponse;
import com.CocOgreen.CenFra.MS.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // 1. Entity -> Response
    // Lấy tên Category từ đối tượng Category lồng bên trong
    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductResponse toResponse(Product product);

    // 2. Request -> Entity
    // categoryId sẽ được xử lý trong Service (tìm trong DB rồi set vào), nên ở đây
    // ignore
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "status", ignore = true) // Mặc định set ACTIVE ở Service
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductRequest request);

    // 3. Update Entity từ Request
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequest request);
}