package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.AssertTrue;
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
    @Min(5)
    @Max(100)
    private Integer quantity;

    @AssertTrue(message = "quantity phải là bội số của 5")
    public boolean isQuantityMultipleOfFive() {
        return quantity == null || quantity % 5 == 0;
    }
}
