package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.CocOgreen.CenFra.MS.enums.CategoryStatus;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CategoryStatus status;

    // Quan hệ 1-N với Product (Optional: để lấy list sản phẩm thuộc danh mục)
    @OneToMany(mappedBy = "category")
    private List<Product> products;
}