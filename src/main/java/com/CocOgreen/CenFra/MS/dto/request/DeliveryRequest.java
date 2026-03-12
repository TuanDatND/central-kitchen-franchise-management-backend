package com.CocOgreen.CenFra.MS.dto.request;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class DeliveryRequest {
    private String driverName;
    private String vehiclePlate;
    private OffsetDateTime scheduledDate;
    private List<Integer> exportNoteIds; // Danh sách ID phiếu xuất (ExportNote) sẽ được gắn vào chuyến xe này
}
