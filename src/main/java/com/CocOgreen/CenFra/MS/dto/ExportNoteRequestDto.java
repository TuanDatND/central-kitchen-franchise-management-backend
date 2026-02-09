package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;


@Data
public class ExportNoteRequestDto {
    private Integer storeOrderId;

    private String note;
}
