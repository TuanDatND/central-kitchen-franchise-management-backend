package com.CocOgreen.CenFra.MS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReceiptResponse {
    private Integer receiptId;
    private String receiptCode;
    private Instant receiptDate;
    private String createdBy;
    private String status;
    private List<ReceiptItemResponse> items; // Danh sách chi tiết

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiptItemResponse {
        private String batchCode;
        private String productName;
        private Integer quantity;
    }
}