package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    
    @Query("SELECT d FROM Delivery d WHERE LOWER(d.deliveryCode) LIKE LOWER(CONCAT('%', :searchword, '%'))")
    Page<Delivery> searchByDeliveryCode(@Param("searchword") String searchword, Pageable pageable);
}
