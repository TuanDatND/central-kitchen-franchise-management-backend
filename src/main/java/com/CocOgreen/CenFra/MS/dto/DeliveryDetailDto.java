package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
    
    // Chi tiết rành mạch các phiếu xuất kho bên trong một chuyến xe
    private List<ExportNoteDto> exportNotes;
}
