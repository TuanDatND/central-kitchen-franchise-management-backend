package com.CocOgreen.CenFra.MS.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ManuOrderRequest {
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<Product> products;

    @Data
    public static class Product {
        @NotNull(message = "Phải chọn sản phẩm để nấu")
        private Integer productId;

        @Min(value = 1, message = "Số lượng nấu phải lớn hơn 0")
        private Integer quantityPlanned;
    }
}