package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Store store;

    @OneToMany(mappedBy = "storeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    private Date deliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreOrderStatus status;

    public StoreOrder(String orderCode, Store store, Date deliveryDate) {
        this.orderCode = orderCode;
        this.store = store;
        this.deliveryDate = deliveryDate;
        this.orderDate = new Date();
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
        if (this.status == StoreOrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        this.status = StoreOrderStatus.CANCELLED;
    }

    public void markConsolidated() {
        if (this.status != StoreOrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED order can be consolidated");
        }
        this.status = StoreOrderStatus.CONSOLIDATED;
    }

    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
        detail.setStoreOrder(this);
    }
}
