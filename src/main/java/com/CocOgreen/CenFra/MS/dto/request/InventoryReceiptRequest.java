package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class InventoryReceiptRequest {
    // Không cần gửi mã phiếu (Backend tự sinh PN-001)

    @NotEmpty(message = "Phiếu nhập phải có ít nhất 1 sản phẩm")
    private List<ReceiptItemRequest> items;

    // Class con (Nested Class) để hứng chi tiết từng dòng nhập
    @Data
    public static class ReceiptItemRequest {
        @NotNull(message = "Phải chọn lô hàng để nhập")
        private Integer productBatchId;

        @NotNull(message = "Phải nhập số lượng thực tế")
        private Integer quantity;
    }
}