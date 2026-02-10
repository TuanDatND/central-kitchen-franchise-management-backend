package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.StoreDTO;
import com.CocOgreen.CenFra.MS.entity.Store;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface StoreMapper {

    StoreDTO toDTO(Store store);
}
