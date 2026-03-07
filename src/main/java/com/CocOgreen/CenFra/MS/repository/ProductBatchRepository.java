package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Integer> {

    // Cần thiết để tìm lô hàng khi quét mã nhập kho
    Optional<ProductBatch> findByBatchCode(String batchCode);

    // Lấy danh sách lô hàng theo trạng thái (Ví dụ: WAITING_FOR_STOCK)
    List<ProductBatch> findByStatus(com.CocOgreen.CenFra.MS.enums.BatchStatus status);

    // Lấy danh sách các lô hàng có trạng thái nhất định và ngày hết hạn trước một
    // ngày cụ thể
    List<ProductBatch> findAllByStatusAndExpiryDateBefore(com.CocOgreen.CenFra.MS.enums.BatchStatus status,
            java.time.LocalDate date);

    // Kiểm tra mã lô trùng lặp khi tạo mới
    boolean existsByBatchCode(String batchCode);

    // Tìm tất cả các lô hàng được tạo ra từ một Lệnh sản xuất cụ thể
    List<ProductBatch> findByManufacturingOrder_ManuOrderId(Integer manuOrderId);

    // TuanDatCutee hehehe
    List<ProductBatch> findByProductAndCurrentQuantityGreaterThanOrderByExpiryDateAsc(Product product,
            Integer quantity);
}