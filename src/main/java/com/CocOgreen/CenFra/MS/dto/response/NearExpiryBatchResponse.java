package com.CocOgreen.CenFra.MS.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearExpiryBatchResponse {
    private String batchCode;
    private String product;
    private LocalDate expiryDate;
    private Integer stock;
}
