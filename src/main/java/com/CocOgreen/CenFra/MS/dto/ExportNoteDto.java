package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

import java.math.BigDecimal;


@Data
public class ExportNoteDto {
    private Integer exportId;
    private String exportCode;
    private Integer storeOrderId;
    private String storeName;
    private String status;
    private OffsetDateTime exportDate;
    private List<ExportItemDto> items;
}
