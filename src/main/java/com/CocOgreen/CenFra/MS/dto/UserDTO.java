package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private String userId;
    private String userName;
    private String fullName;
    private String role;
}
