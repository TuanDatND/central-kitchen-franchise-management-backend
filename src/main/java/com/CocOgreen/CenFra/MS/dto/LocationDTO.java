package com.CocOgreen.CenFra.MS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {

    private Integer locationId;
    private String locationName;

    private Integer locationTypeId;
    private String locationTypeCode;

    private String address;
    private String phone;

    private Boolean isDeleted;
}
