package com.CocOgreen.CenFra.MS.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class InventoryTransactionDto {
    private Integer transactionId;
    private String productName;      // Join qua ProductBatch -> Product
    private String batchCode;
    private String transactionType;  // IMPORT, EXPORT, DISPOSAL
    private Integer quantity;        // Ví dụ: -50 hoặc +100
    private String referenceCode;    // Mã PN-xxx hoặc PX-xxx
    private OffsetDateTime transactionDate;
    private String createdByFullName;
    private String note;
}
