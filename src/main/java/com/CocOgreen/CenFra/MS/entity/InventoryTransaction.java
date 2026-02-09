package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "export_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_item_id")
    private Integer exportItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "export_id")
    private ExportNote exportNote;

    // Liên kết với Lô hàng của Dev 2
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_batch_id")
    private ProductBatch productBatch;

    @Column(nullable = false)
    private Integer quantity;
}
