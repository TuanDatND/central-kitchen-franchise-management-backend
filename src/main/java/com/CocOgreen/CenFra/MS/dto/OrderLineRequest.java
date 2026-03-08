package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineRequest {
    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer quantity;
}
