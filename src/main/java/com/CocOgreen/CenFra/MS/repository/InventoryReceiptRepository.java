package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.InventoryReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryReceiptRepository extends JpaRepository<InventoryReceipt, Integer> {

    // Tìm phiếu nhập theo mã phiếu
    Optional<InventoryReceipt> findByReceiptCode(String receiptCode);
}