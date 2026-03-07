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
}