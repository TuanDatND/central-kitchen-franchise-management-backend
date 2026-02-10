
package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Integer> {

    Optional<StoreOrder> findByOrderCode(String orderCode);

    List<StoreOrder> findByStatus(StoreOrderStatus status);

    List<StoreOrder> findByStore_StoreId(Integer storeId);

    List<StoreOrder> findByStore_StoreIdAndStatus(Integer storeId, StoreOrderStatus status);
}
