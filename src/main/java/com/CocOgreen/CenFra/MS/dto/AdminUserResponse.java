package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private RoleName role;
    private Integer storeId;
    private String storeName;
    private UserStatus status;
}
