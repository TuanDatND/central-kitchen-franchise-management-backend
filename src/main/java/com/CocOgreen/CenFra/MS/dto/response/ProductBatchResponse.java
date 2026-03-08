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
    private String productName;
    private Integer currentQuantity;// Tồn kho thực tế (Quan trọng cho Dev 3)
    private Integer initialQuantity;// Số lượng ban đầu
    private String unitName;
    private LocalDate expiryDate; // Hạn sử dụng (Quan trọng cho Dev 3 - FEFO)
    private String status; // AVAILABLE, EXPIRED...
}