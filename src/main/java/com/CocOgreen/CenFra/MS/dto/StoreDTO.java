package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreDTO {

    private Integer storeId;
    private String storeName;
    private String address;
    private String phone;

    private UserDTO manager;   // Store Manager (DTO, không phải Entity)

}
