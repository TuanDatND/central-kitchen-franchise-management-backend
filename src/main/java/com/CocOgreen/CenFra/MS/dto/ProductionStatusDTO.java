package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

@Data
public class ProductionStatusDTO {
    private Integer id;
    private String code; // PLANNED, COOKING, COMPLETED
}
