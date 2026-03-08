package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.Instant;

@Data
public class ManuOrderRequest {
    @NotNull(message = "Phải chọn sản phẩm để nấu")
    private Integer productId;

    @Min(value = 1, message = "Số lượng nấu phải lớn hơn 0")
    private Integer quantity; // Số lượng dự kiến nấu

}