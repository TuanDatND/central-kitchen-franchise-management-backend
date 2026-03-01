package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {
    @NotNull
    private RoleName role;
}
