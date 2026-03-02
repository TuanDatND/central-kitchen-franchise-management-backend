package com.CocOgreen.CenFra.MS.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreInfoRequest {
    @Size(max = 255)
    private String storeName;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String phone;
}
