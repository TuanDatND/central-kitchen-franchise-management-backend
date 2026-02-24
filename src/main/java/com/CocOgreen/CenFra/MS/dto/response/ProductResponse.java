package com.CocOgreen.CenFra.MS.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Integer productId;
    private String productName;
    private String unit;
    private String imageUrl;
    private String description;
    private String status;       // ACTIVE/INACTIVE
    private String categoryName; // Tên danh mục (đã join bảng)
}