
package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Integer> {

    Optional<StoreOrder> findByOrderCode(String orderCode);

    Page<StoreOrder> findByStatus(StoreOrderStatus status, Pageable pageable);

    Page<StoreOrder> findByStore_StoreId(Integer storeId, Pageable pageable);

    Page<StoreOrder> findByStore_StoreIdAndStatus(Integer storeId, StoreOrderStatus status, Pageable pageable);

    long countByStore_StoreId(Integer storeId);

    long countByStatus(StoreOrderStatus status);

    long countByOrderDateBetween(Date fromInclusive, Date toExclusive);

    long countByStatusAndOrderDateBetween(StoreOrderStatus status, Date fromInclusive, Date toExclusive);

    @Query("""
            select s.storeId as storeId, s.storeName as storeName, count(so.orderId) as totalOrders
              from StoreOrder so
              join so.store s
             group by s.storeId, s.storeName
             order by count(so.orderId) desc
            """)
    java.util.List<TopStoreOrderProjection> findTopStoresByOrderCount(Pageable pageable);

    @Query("""
            select distinct so
              from StoreOrder so
              join fetch so.orderDetails od
              join fetch od.product p
             where so.status = :status
               and p.productId = :productId
            """)
    List<StoreOrder> findDistinctByStatusAndProductId(StoreOrderStatus status, Integer productId);
}
