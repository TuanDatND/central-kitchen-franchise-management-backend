package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

    default LocalDateTime mapToLocalDateTime(Date value) {
        if (value == null) {
            return null;
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    default LocalDate mapToLocalDate(Date value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
