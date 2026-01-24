package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDTO {

    private Integer userId;

    private String username;
    private String password;   // raw khi create/update

    private String fullName;

    private Long roleId;
    private String roleName;

    private Integer locationId;
    private String locationName;

    private Boolean isDeleted;
}
