package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private RoleName role;
    private Boolean isActive;
}
