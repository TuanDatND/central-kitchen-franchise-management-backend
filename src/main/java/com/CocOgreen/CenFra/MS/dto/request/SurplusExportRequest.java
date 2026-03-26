package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO dùng cho yêu cầu xuất kho thặng dư (Surplus Export).
 * Khác với ManualExportRequest: không cần storeOrderId.
 * Dùng trong trường hợp nhân viên kho lấy hàng dư hoặc
 * đồng bộ tồn kho thực tế so với hệ thống.
 */
@Data
public class SurplusExportRequest {

    /**
     * Lý do xuất kho thặng dư. Bắt buộc phải có để
     * ghi chú rõ ràng trong sổ cái InventoryTransaction.
     * Ví dụ: "Xuất hàng dư kiểm kê Q1", "Điều chỉnh tồn kho"
     */
    @NotEmpty(message = "Lý do xuất kho không được để trống")
    private String reason;

    /**
     * Danh sách các lô hàng cần xuất, kèm số lượng.
     * Phải có ít nhất 1 item.
     */
    @NotEmpty(message = "Danh sách lô hàng không được để trống")
    private List<Item> items;

    /**
     * Chi tiết từng lô hàng cần xuất.
     */
    @Data
    public static class Item {
        /**
         * ID của lô hàng (ProductBatch).
         * Lô hàng phải có trạng thái AVAILABLE.
         */
        @NotNull(message = "Batch ID không được để trống")
        private Integer batchId;

        /**
         * Số lượng cần xuất từ lô hàng này.
         * Phải lớn hơn 0 và không được vượt quá currentQuantity của lô.
         */
        @Min(value = 1, message = "Số lượng phải ít nhất là 1")
        private Integer quantity;
    }
}
