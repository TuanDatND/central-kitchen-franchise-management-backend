package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDTO {

    private Integer unitId;
    private String unitName;
    private Boolean isDeleted;
}
