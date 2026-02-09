package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class ExportNoteRequestDto {
    @NotNull(message = "ID đơn hàng không được để trống")
    private Integer storeOrderId;

    private String note;
}
