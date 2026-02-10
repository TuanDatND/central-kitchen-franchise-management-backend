package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "export_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_batch_id")
    private ProductBatch productBatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Integer quantity; // Số lượng dương (nhập) hoặc âm (xuất)

    @Column(name = "reference_code")
    private String referenceCode; // Mã PN hoặc PX

    @Column(name = "transaction_date")
    private OffsetDateTime transactionDate = OffsetDateTime.now();

    private String note;
}
