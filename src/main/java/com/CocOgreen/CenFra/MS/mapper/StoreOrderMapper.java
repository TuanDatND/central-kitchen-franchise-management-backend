package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        uses = OrderDetailMapper.class
)
public interface StoreOrderMapper {

    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "storeName", source = "store.storeName")
    @Mapping(target = "details", source = "orderDetails")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(storeOrder))")
    StoreOrderDTO toDTO(StoreOrder storeOrder);

    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "storeName", source = "store.storeName")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(storeOrder))")
    @Mapping(target = "details", expression = "java(java.util.Collections.emptyList())")
    StoreOrderDTO toSummaryDTO(StoreOrder storeOrder);

    default BigDecimal calculateTotalAmount(StoreOrder storeOrder) {
        if (storeOrder.getOrderDetails() == null) {
            return BigDecimal.ZERO;
        }
        return storeOrder.getOrderDetails().stream()
                .map(this::calculateLineTotal)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal calculateLineTotal(OrderDetail detail) {
        if (detail.getUnitPrice() == null || detail.getQuantity() == null) {
            return null;
        }
        return detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
    }
}
