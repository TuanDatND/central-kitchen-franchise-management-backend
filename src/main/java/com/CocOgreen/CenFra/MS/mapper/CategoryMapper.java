package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.request.CategoryRequest;
import com.CocOgreen.CenFra.MS.dto.response.CategoryResponse;
import com.CocOgreen.CenFra.MS.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity -> Response
    CategoryResponse toResponse(Category category);

    // Request -> Entity (Bỏ qua ID vì tự tăng)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products", ignore = true) // List sản phẩm không cần map khi tạo
    @Mapping(target = "status", ignore = true)
    Category toEntity(CategoryRequest request);
}