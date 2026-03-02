package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserActiveRequest {
    @NotNull
    private Boolean isActive;
}
