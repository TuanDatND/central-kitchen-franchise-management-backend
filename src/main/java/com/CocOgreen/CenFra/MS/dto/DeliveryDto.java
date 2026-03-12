package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class DeliveryDto {
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
    
    // Tóm tắt các phiếu xuất đi kèm
    private List<ExportNoteSummaryDto> exportNotes;

    @Data
    public static class ExportNoteSummaryDto {
        private Integer exportId;
        private String exportCode;
        private String storeName;
        private String status;
    }
}
