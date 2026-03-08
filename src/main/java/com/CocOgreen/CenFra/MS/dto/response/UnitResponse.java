package com.CocOgreen.CenFra.MS.dto.response;

import com.CocOgreen.CenFra.MS.enums.UnitStatus;
import lombok.Data;

@Data
public class UnitResponse {
    private Integer unitId;
    private String unitName;
    private String description;
    private UnitStatus status;
}
