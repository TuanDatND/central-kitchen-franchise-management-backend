package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "product_batches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "batch_code", nullable = false, unique = true, length = 50)
    private String batchCode;

    // FK: product_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // FK: manu_order_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manu_order_id")
    private ManufacturingOrder manufacturingOrder;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BatchStatus status;
}