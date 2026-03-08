package com.CocOgreen.CenFra.MS.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request DTO cho Unit (Đơn vị tính)")
public class UnitRequest {

    @NotBlank(message = "Tên đơn vị không được để trống")
    @Schema(description = "Tên đơn vị", example = "kg")
    private String unitName;

    @Schema(description = "Mô tả đơn vị", example = "Kilogram")
    private String description;
}
