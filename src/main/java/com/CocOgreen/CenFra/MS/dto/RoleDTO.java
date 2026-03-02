package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDTO {

    private Integer roleId;
    private String roleName; // ADMIN, MANAGER, SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF, FRANCHISE_STORE_STAFF
}
