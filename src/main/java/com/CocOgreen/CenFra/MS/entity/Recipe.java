package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recipe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recipeId;

    @Column(precision = 18, scale = 4)
    private BigDecimal yield;//Số lượng đầu ra

    @Column
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_item_id")
    private Item outputItem;

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeDetail> recipeDetail;

}
