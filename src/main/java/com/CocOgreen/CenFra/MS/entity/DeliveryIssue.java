package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.DeliveryIssueDecision;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueReason;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueResolution;
import com.CocOgreen.CenFra.MS.enums.DeliveryIssueStatus;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_order_id", nullable = false)
    private StoreOrder storeOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replacement_order_id")
    private StoreOrder replacementOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryIssueStatus status = DeliveryIssueStatus.PENDING_REVIEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_order_status", nullable = false)
    private StoreOrderStatus reportedOrderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_reason", nullable = false)
    private DeliveryIssueReason reason;

    @Column(name = "issue_note", length = 2000)
    private String note;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "affected_quantity")
    private Integer affectedQuantity;

    @Column(name = "issue_items_json", columnDefinition = "TEXT")
    private String issueItemsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_resolution")
    private DeliveryIssueResolution recommendedResolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_decision")
    private DeliveryIssueDecision reviewDecision;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_resolution")
    private DeliveryIssueResolution selectedResolution;

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;
}
