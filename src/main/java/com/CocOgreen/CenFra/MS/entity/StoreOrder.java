package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "storeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private LocalDateTime orderDate;

    private LocalDate deliveryDate;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreOrderStatus status;

    public StoreOrder(String orderCode, Store store, LocalDate deliveryDate) {
        this.orderCode = orderCode;
        this.store = store;
        this.deliveryDate = deliveryDate;
        this.orderDate = LocalDateTime.now();
        this.status = StoreOrderStatus.PENDING;
        this.orderDetails = new ArrayList<>();
    }

    public void approve() {
        if (this.status != StoreOrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING order can be approved");
        }
        this.status = StoreOrderStatus.APPROVED;
    }

    public void cancel() {
        if (this.status != StoreOrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING order can be cancelled");
        }
        this.status = StoreOrderStatus.CANCELLED;
    }

    public void markConsolidated() {
        if (this.status != StoreOrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED order can be consolidated");
        }
        this.status = StoreOrderStatus.CONSOLIDATED;
    }

    public void markAsReceived() {
        if (this.status != StoreOrderStatus.IN_TRANSIT) {
            throw new IllegalStateException("Only IN_TRANSIT order can be marked as DONE");
        }
        this.status = StoreOrderStatus.DONE;
        this.receivedAt = LocalDateTime.now();
    }

    public void markDeliveryIssuePending() {
        if (this.status != StoreOrderStatus.IN_TRANSIT && this.status != StoreOrderStatus.DONE) {
            throw new IllegalStateException("Only IN_TRANSIT or DONE order can be marked as DELIVERY_ISSUE_PENDING");
        }
        this.status = StoreOrderStatus.DELIVERY_ISSUE_PENDING;
    }

    public void markDeliveryFailed() {
        if (this.status != StoreOrderStatus.DELIVERY_ISSUE_PENDING && this.status != StoreOrderStatus.IN_TRANSIT) {
            throw new IllegalStateException("Only IN_TRANSIT or DELIVERY_ISSUE_PENDING order can be marked as DELIVERY_FAILED");
        }
        this.status = StoreOrderStatus.DELIVERY_FAILED;
    }

    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
        detail.setStoreOrder(this);
    }
}
