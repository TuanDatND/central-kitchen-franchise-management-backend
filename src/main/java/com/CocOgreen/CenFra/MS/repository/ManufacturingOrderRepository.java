package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ManufacturingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturingOrderRepository extends JpaRepository<ManufacturingOrder, Integer> {

    // Dùng để tìm lệnh sản xuất khi Bếp trưởng nhập mã hoặc quét QR code
    Optional<ManufacturingOrder> findByOrderCode(String orderCode);

    // Kiểm tra xem mã lệnh đã tồn tại chưa (Validation)
    boolean existsByOrderCode(String orderCode);
}
