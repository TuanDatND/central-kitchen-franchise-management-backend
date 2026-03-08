package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
    boolean existsByUnitNameIgnoreCase(String unitName);
}
