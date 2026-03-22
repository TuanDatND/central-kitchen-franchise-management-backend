package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    // FK: unit_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status;

    @Column(name = "price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "order_multiplier")
    private Integer orderMultiplier = 1;

    // FK: category_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}