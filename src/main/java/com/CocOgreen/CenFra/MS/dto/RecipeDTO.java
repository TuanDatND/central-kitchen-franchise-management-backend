package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RecipeDTO {
    private Integer recipeId;
    private Integer outputItemId;  // ID thành phẩm
    private String outputItemName; // Tên thành phẩm
    private BigDecimal yield;      // Sản lượng chuẩn (VD: nấu 1 mẻ được 10 lít)
    private List<RecipeDetailDTO> details; // Danh sách nguyên liệu
}
