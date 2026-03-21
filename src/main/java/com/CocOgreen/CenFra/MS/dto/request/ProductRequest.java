package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    @NotNull(message = "Phải chọn đơn vị tính")
    private Integer unitId;

    private String imageUrl;
    private String description;

    @NotNull(message = "Phải chọn danh mục")
    private Integer categoryId; // Gửi ID của Category

    @jakarta.validation.constraints.Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private java.math.BigDecimal price;

    @jakarta.validation.constraints.Min(value = 1, message = "Hạn sử dụng phải từ 1 ngày trở lên")
    private Integer shelfLifeDays;

    @NotNull(message = "Order multiplier không được để trống")
    @jakarta.validation.constraints.Min(value = 1, message = "Order multiplier must be at least 1")
    private Integer orderMultiplier;
}