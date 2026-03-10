package com.CocOgreen.CenFra.MS.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.CocOgreen.CenFra.MS.enums.InventoryStockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryResponse {
    private String productName;
    private Integer productId;
    private Integer batchId;
    private LocalDate expiryDate;
    private String warning;
    private String unit;
    private Long totalStock;
    private InventoryStockStatus status;
    private List<ProductBatchResponse> productBatch;

}
