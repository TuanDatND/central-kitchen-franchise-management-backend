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
    private String status;

    // Đối tượng User
    private Integer createdById;
    private String createdByName;

    // Danh sách chi tiết
    private List<ReceiptItemResponse> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiptItemResponse {
        private Integer receiptItemId;
        private Integer quantity;

        // Đối tượng ProductBatch
        private Integer batchId;
        private String batchCode;
    }
}