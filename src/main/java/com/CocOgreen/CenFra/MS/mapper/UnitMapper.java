package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.request.UnitRequest;
import com.CocOgreen.CenFra.MS.dto.response.UnitResponse;
import com.CocOgreen.CenFra.MS.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    Unit toEntity(UnitRequest request);

    UnitResponse toResponse(Unit entity);

    void updateEntityFromRequest(UnitRequest request, @MappingTarget Unit entity);
}
