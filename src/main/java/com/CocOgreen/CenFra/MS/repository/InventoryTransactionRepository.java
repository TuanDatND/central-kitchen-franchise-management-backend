package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    // Tìm lịch sử biến động của một lô hàng cụ thể
    List<InventoryTransaction> findByProductBatch_BatchIdOrderByTransactionDateDesc(Integer batchId);

    // Tìm theo mã phiếu (PN hoặc PX) để kiểm tra đối soát
    List<InventoryTransaction> findByReferenceCode(String referenceCode);
}
