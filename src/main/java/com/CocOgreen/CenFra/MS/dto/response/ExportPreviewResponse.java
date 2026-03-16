package com.CocOgreen.CenFra.MS.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về kết quả xem trước (preview) việc xuất kho theo thuật toán FEFO.
 * Frontend dùng response này để hiển thị cho SUPPLY_COORDINATOR xem
 * hệ thống dự định lấy từ lô (batch) nào trước khi xác nhận tạo phiếu xuất.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xem trước kế hoạch xuất kho FEFO cho một đơn hàng")
public class ExportPreviewResponse {

    @Schema(description = "ID của đơn hàng Franchise Store", example = "5")
    private Integer storeOrderId;

    @Schema(description = "Mã đơn hàng", example = "SO-2024-005")
    private String orderCode;

    @Schema(description = "Tên cửa hàng đã đặt đơn", example = "Cửa hàng Quận 1")
    private String storeName;

    @Schema(
        description = "true = kho đủ hàng để xuất toàn bộ đơn này | false = kho THIẾU ít nhất 1 sản phẩm",
        example = "true"
    )
    private boolean canFulfill;

    @Schema(description = "Danh sách từng sản phẩm trong đơn hàng và kế hoạch lấy lô hàng tương ứng")
    private List<ProductPreviewItem> products;

    // =========================================================
    // Thông tin từng sản phẩm trong đơn hàng
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Thông tin preview cho một sản phẩm trong đơn hàng")
    public static class ProductPreviewItem {

        @Schema(description = "ID sản phẩm", example = "12")
        private Integer productId;

        @Schema(description = "Tên sản phẩm", example = "Phở bò tái")
        private String productName;

        @Schema(description = "Đơn vị tính của sản phẩm", example = "kg")
        private String unit;

        @Schema(
            description = "Số lượng mà đơn hàng YÊU CẦU",
            example = "100"
        )
        private Integer requiredQuantity;

        @Schema(
            description = "Số lượng kho CÓ THỂ ĐÁP ỨNG được (= requiredQuantity - shortfall). "
                        + "Nếu bằng requiredQuantity → đủ hàng hoàn toàn.",
            example = "100"
        )
        private Integer fulfillableQuantity;

        @Schema(
            description = "Số lượng BỊ THIẾU. "
                        + "0 = đủ hàng | >0 = thiếu, không nên tạo phiếu xuất khi shortfall > 0",
            example = "0"
        )
        private Integer shortfall;

        @Schema(description = "Danh sách các lô hàng (batch) sẽ được lấy, sắp xếp theo FEFO (gần hết hạn nhất lấy trước)")
        private List<BatchAllocation> batchAllocations;
    }

    // =========================================================
    // Thông tin từng lô hàng được phân bổ
    // =========================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chi tiết một lô hàng (batch) được phân bổ để xuất kho")
    public static class BatchAllocation {

        @Schema(description = "ID lô hàng trong hệ thống", example = "7")
        private Integer batchId;

        @Schema(description = "Mã lô hàng", example = "LOT-20240206-A")
        private String batchCode;

        @Schema(
            description = "Ngày hết hạn của lô — FEFO ưu tiên lô có expiryDate sớm nhất",
            example = "2024-03-01"
        )
        private LocalDate expiryDate;

        @Schema(description = "Ngày sản xuất của lô", example = "2024-02-06")
        private LocalDate manufacturingDate;

        @Schema(
            description = "Tồn kho HIỆN TẠI của lô tại thời điểm preview (đã trừ các order trước trong cùng lần preview)",
            example = "80"
        )
        private Integer currentStock;

        @Schema(
            description = "Số lượng SẼ ĐƯỢC LẤY từ lô này để phục vụ đơn hàng (allocatedQuantity ≤ currentStock)",
            example = "60"
        )
        private Integer allocatedQuantity;
    }
}
