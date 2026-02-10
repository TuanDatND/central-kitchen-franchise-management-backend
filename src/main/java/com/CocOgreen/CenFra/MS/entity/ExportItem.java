package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "export_items")
public class ExportItem {
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
