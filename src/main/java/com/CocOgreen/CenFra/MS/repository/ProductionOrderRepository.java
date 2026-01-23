package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Integer> {
}
