package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.stream.Location;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; // Bếp thực hiện sản xuất

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe; // Công thức sử dụng

    @Column(precision = 18, scale = 4)
    private BigDecimal planQty; // Số lượng dự kiến nấu

    @Column(precision = 18, scale = 4)
    private BigDecimal actualQty; // Số lượng thực tế thu được

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_status_id")
    private ProductionStatus status; // PLANNED, COOKING, COMPLETED

    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
