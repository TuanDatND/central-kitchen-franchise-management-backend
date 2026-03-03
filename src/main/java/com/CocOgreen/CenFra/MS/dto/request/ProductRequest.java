package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;

    private String imageUrl;
    private String description;

    @NotNull(message = "Phải chọn danh mục")
    private Integer categoryId; // Gửi ID của Category
}