package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO {

    private Integer roleId;
    private String roleName; // ADMIN, STORE_MANAGER, ...
}
