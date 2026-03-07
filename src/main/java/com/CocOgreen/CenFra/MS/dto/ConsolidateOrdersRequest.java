package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsolidateOrdersRequest {

    @NotNull
    private Integer productId;

    private List<Integer> orderIds;

}
