package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Integer> {

    // Lấy danh sách chi tiết của một phiếu nhập cụ thể (để xem lại lịch sử nhập)
    List<ReceiptItem> findByInventoryReceipt_ReceiptId(Integer receiptId);
}