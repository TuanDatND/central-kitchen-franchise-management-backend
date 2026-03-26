package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO cho việc tạo Lệnh Sản Xuất thủ công (replenishment).
 * Thuộc phạm vi của Backend Dev 2 (Master Data & Inbound).
 */
@Data
public class ManualManuOrderRequest {

    // ID chung của sản phẩm (Master Data)
    @NotNull(message = "Product ID không được để trống")
    private Integer productId;

    // Số lượng kế hoạch nấu
    @NotNull(message = "Quantity planned không được để trống")
    @Min(value = 1, message = "Số lượng kế hoạch phải lớn hơn 0")
    private Integer quantityPlanned;
}
