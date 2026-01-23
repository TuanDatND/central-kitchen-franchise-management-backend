package com.CocOgreen.CenFra.MS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productionStatusID;

    @Column
    private String code;
}
