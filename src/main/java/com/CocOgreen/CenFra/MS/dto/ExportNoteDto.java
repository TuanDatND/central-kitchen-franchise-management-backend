package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ExportNoteDto {
    private Integer exportId;
    private String exportCode;
    private Integer storeOrderId;
    private String storeName; // Lấy từ bảng Store qua StoreOrder
    private String status;
    private OffsetDateTime exportDate;
    private List<ExportItemDto> items; // Chi tiết các mặt hàng đã xuất
}
