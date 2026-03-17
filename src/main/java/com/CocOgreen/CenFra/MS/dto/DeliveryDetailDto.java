package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class DeliveryDetailDto {
    private Integer deliveryId;
    private String deliveryCode;
    private String driverName;
    private String vehiclePlate;
    private OffsetDateTime scheduledDate;
    private OffsetDateTime actualStartDate;
    private OffsetDateTime actualEndDate;
    private String status;
    private String createdByUsername;
    private OffsetDateTime createdAt;
    
    // Chi tiết rành mạch các phiếu xuất kho bên trong một chuyến xe (chỉ dùng khi chuyến chưa bị hủy)
    private List<ExportNoteDto> exportNotes;

    /**
     * Snapshot JSON toàn bộ thông tin phiếu xuất trước khi bị hủy.
     * Chỉ có giá trị khi status = CANCELLED.
     * Bao gồm: mã phiếu, cửa hàng, sản phẩm, số lượng, lô hàng...
     */
    private String cancelledNotesSnapshot;
}
