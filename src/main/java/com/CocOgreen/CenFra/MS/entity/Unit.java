package com.CocOgreen.CenFra.MS.entity;

import com.CocOgreen.CenFra.MS.enums.UnitStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "unit_name", nullable = false, length = 50, unique = true)
    private String unitName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UnitStatus status = UnitStatus.ACTIVE;
}
