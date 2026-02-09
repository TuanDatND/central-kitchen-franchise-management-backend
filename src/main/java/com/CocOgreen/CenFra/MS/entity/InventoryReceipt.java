package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.ReceiptStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "inventory_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Integer receiptId;

    @Column(name = "receipt_code", nullable = false, unique = true, length = 50)
    private String receiptCode;

    @Column(name = "receipt_date")
    private Instant receiptDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReceiptStatus status;

    // FK: created_by
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;

    // Quan hệ 1-N: Một phiếu nhập có nhiều dòng chi tiết
    @OneToMany(mappedBy = "inventoryReceipt", cascade = CascadeType.ALL)
    private List<ReceiptItem> receiptItems;
}