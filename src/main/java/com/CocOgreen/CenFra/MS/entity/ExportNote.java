
package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "export_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_id")
    private Integer exportId;

    @Column(name = "export_code", unique = true, nullable = false)
    private String exportCode;

    // Quan hệ với đơn hàng của Dev 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_order_id")
    private StoreOrder storeOrder;

    @Column(name = "export_date")
    private OffsetDateTime exportDate = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExportStatus status = ExportStatus.READY;

    // Quan hệ với người tạo (Dev 1 quản lý bảng users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Cập nhật hệ thống giao hàng Dev 3 mới
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "exportNote", cascade = CascadeType.ALL)
    private List<ExportItem> items;
}
