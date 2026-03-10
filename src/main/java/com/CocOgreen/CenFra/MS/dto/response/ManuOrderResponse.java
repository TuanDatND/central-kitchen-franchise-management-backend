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
    private Instant startDate;
    private Instant endDate;
    private String status; // PLANNED, COOKING...
    private Integer quantityPlanned; // Số lượng dự kiến

    // Thuộc tính đối tượng Product
    private Integer productId;
    private String productName;

    // Thuộc tính đối tượng User
    private Integer createdById;
    private String createdByName; // Tên người tạo
}