package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecipeDetailDTO {
    private Integer detailId;
    private Integer inputItemId;   // ID nguyên liệu
    private String inputItemName;  // Tên nguyên liệu (để hiện lên web cho nhanh)
    private BigDecimal quantity;   // Định mức
}
