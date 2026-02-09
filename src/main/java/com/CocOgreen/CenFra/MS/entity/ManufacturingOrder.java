package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.ManuOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant; // Dùng Instant cho TIMESTAMP WITH TIME ZONE

@Entity
@Table(name = "manufacturing_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manu_order_id")
    private Integer manuOrderId;

    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    private String orderCode;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ManuOrderStatus status;

    // FK: created_by (User)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
}