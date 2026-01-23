package com.CocOgreen.CenFra.MS.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductionOrderDTO {
    private Integer prodId;
    private Integer locationId;    // Bếp nào nấu
    private String locationName;

    private Integer recipeId;      // Nấu theo công thức nào
    private String recipeName;

    private BigDecimal planQty;    // Định nấu bao nhiêu
    private BigDecimal actualQty;  // Thực tế thu được bao nhiêu

    private Integer statusId;      // Trạng thái (ID)
    private String statusName;     // Trạng thái (Tên để hiện: "Đang nấu")

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
