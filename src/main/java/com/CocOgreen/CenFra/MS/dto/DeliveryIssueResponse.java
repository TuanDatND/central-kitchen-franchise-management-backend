package com.CocOgreen.CenFra.MS.dto;

import com.CocOgreen.CenFra.MS.enums.DeliveryIssueDecision;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueReason;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueResolution;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryIssueResponse {
    private Integer issueId;
    private DeliveryIssueStatus issueStatus;
    private StoreOrderStatus reportedOrderStatus;
    private DeliveryIssueReason issueReason;
    private String issueNote;
    private Integer totalQuantity;
    private Integer affectedQuantity;
    private List<DeliveryIssueItemResponse> issueItems;
    private DeliveryIssueResolution recommendedResolution;
    private DeliveryIssueResolution selectedResolution;
    private Integer originalOrderId;
    private String originalOrderCode;
    private StoreOrderStatus originalOrderStatus;
    private Integer storeId;
    private String storeName;
    private LocalDate originalDeliveryDate;
    private OrderActionActorDTO reportedBy;
    private LocalDateTime reportedAt;
    private OrderActionActorDTO reviewedBy;
    private LocalDateTime reviewedAt;
    private DeliveryIssueDecision reviewDecision;
    private Integer replacementOrderId;
    private String replacementOrderCode;
    private List<String> imageUrls;
}
