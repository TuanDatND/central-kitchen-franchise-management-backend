package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.DeliveryIssueReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectDeliveryRequest {

    @NotNull(message = "Lý do không được để trống")
    private DeliveryIssueReason reason;

    @Size(max = 2000, message = "Ghi chú không được vượt quá 2000 ký tự")
    private String note;

    private List<DeliveryIssueItemRequest> items;
}
