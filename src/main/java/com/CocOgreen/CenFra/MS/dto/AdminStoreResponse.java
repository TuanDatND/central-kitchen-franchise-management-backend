package com.CocOgreen.CenFra.MS.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminStoreResponse {
    private Integer storeId;
    private String storeName;
    private String address;
    private String phone;
    private Boolean isActive;
}
