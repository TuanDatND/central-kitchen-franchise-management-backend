package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStoreOrderRequest {
    private Integer storeId;

    @NotNull
    @FutureOrPresent
    private LocalDate deliveryDate;

    @NotEmpty
    @Valid
    private List<OrderLineRequest> details;
}
