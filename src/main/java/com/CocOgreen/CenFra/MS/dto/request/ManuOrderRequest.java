package com.CocOgreen.CenFra.MS.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ManuOrderRequest {
    private Integer productId;
    private Integer quantity;
    private LocalDateTime startDate; // Frontend gửi: "2024-02-10T08:00:00"
}