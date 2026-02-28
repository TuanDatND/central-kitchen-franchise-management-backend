
package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Integer> {

    Optional<StoreOrder> findByOrderCode(String orderCode);

    Page<StoreOrder> findByStatus(StoreOrderStatus status, Pageable pageable);

    Page<StoreOrder> findByStore_StoreId(Integer storeId, Pageable pageable);

    Page<StoreOrder> findByStore_StoreIdAndStatus(Integer storeId, StoreOrderStatus status, Pageable pageable);

    long countByStatus(StoreOrderStatus status);

    @Query("""
            select s.storeId as storeId, s.storeName as storeName, count(so.orderId) as totalOrders
              from StoreOrder so
              join so.store s
             group by s.storeId, s.storeName
             order by count(so.orderId) desc
            """)
    java.util.List<TopStoreOrderProjection> findTopStoresByOrderCount(Pageable pageable);
}
