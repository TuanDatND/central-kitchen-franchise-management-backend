package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.DeliveryDto;
import com.CocOgreen.CenFra.MS.entity.Delivery;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "createdByUsername", source = "createdBy.userName")
    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "storeName", source = "storeOrder.store.storeName")
    DeliveryDto.ExportNoteSummaryDto toSummaryDto(ExportNote exportNote);
}
