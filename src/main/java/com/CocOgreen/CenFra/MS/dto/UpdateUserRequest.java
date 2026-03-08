package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.UserStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String fullName;

    private String email;

    @Size(min = 6, max = 100)
    private String password;

    private RoleName role;

    private Integer storeId;

    private UserStatus status;
}
