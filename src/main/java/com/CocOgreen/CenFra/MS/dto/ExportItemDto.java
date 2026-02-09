package com.CocOgreen.CenFra.MS.dto;

import java.time.LocalDate;

public class ExportItemDto {
    private Integer productId;
    private String productName;
    private String batchCode;     // Mã lô để nhân viên kho tìm hàng
    private LocalDate expiryDate; // Để nhân viên đối chiếu hạn sử dụng
    private Integer quantity;     // Số lượng xuất từ lô này
    private String unit;          // Đơn vị tính (kg, lit...)
}
