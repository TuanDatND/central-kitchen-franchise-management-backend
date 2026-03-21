package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.DeliveryIssueDecision;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDeliveryIssueRequest {

    @NotNull(message = "Quyết định xử lý issue không được để trống")
    private DeliveryIssueDecision decision;

    private LocalDate newDeliveryDate;
}
