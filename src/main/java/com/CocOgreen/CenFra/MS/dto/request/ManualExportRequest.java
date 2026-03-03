package com.CocOgreen.CenFra.MS.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ManualExportRequest {
    private Integer storeOrderId;
    private List<Item> selectedBatches;

    @Data
    public static class Item {
        private Integer batchId;
        private Integer quantity;
    }
}
