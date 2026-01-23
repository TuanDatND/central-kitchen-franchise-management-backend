package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionStatusRepository extends JpaRepository<ProductionStatus, Integer> {
}
