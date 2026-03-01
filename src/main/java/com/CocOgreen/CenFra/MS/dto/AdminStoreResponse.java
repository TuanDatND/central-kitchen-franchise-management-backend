package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStoreResponse {
    private Integer storeId;
    private String storeName;
    private String address;
    private String phone;
    private Boolean isActive;
    private Integer managerUserId;
    private String managerUsername;
    private String managerFullName;
}
