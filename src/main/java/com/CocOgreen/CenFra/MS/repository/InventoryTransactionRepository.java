package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {

    Page<InventoryTransaction> findAll(Pageable pageable);

    // Tìm theo mã phiếu (PN hoặc PX) để kiểm tra đối soát
    Page<InventoryTransaction> findByReferenceCodeContaining(String referenceCode, Pageable pageable);
}
