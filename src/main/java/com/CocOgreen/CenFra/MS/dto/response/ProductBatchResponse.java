package com.CocOgreen.CenFra.MS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBatchResponse {
    private Integer batchId;
    private String batchCode; // LOT-BEEF-001
    private Integer initialQuantity; // Số lượng ban đầu
    private Integer currentQuantity; // Tồn kho thực tế
    private LocalDate manufacturingDate;
    private LocalDate expiryDate; // Hạn sử dụng
    private String status; // AVAILABLE, EXPIRED...

    // Thuộc tính đối tượng Product
    private Integer productId;
    private String productName;

    // Thuộc tính đối tượng ManufacturingOrder
    private Integer manuOrderId;
    private String manuOrderCode;
}