package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.OrderDetailDTO;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(source = "detailId", target = "orderDetailId")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unit.unitName", target = "unitName")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(target = "lineTotal", expression = "java(calculateLineTotal(entity))")
    OrderDetailDTO toDto(OrderDetail entity);

    @Mapping(target = "storeOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderDetail toEntity(OrderDetailDTO dto);

    default BigDecimal calculateLineTotal(OrderDetail entity) {
        if (entity.getUnitPrice() == null || entity.getQuantity() == null) {
            return null;
        }
        return entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
    }

}
