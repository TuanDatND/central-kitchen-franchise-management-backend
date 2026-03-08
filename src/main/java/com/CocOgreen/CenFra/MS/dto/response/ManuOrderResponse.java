package com.CocOgreen.CenFra.MS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManuOrderResponse {
    private Integer manuOrderId;
    private String orderCode; // MO-2024...
    private String productName; // Tên món
    private Integer quantity; // Số lượng
    private String unitName; // Đơn vị
    private String status; // PLANNED, COOKING...
    private Instant startDate;
    private String createdBy; // Tên người tạo
}