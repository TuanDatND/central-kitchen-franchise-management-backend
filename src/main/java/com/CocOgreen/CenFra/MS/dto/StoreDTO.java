package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.StoreStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreDTO {
    private Integer storeId;
    private String storeName;
    private String address;
    private String phone;
    private StoreStatus status;
}
