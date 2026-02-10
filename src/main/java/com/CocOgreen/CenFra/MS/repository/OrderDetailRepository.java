package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByStoreOrder_OrderId(Integer orderId);
}
