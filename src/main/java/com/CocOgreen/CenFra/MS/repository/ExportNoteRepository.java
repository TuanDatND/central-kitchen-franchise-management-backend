package com.CocOgreen.CenFra.MS.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;


public interface ExportNoteRepository extends JpaRepository<ExportNote, Integer> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"storeOrder", "storeOrder.store"})
    @Query("SELECT e FROM ExportNote e WHERE LOWER(e.exportCode) LIKE LOWER(CONCAT('%', :exportCode, '%'))")
    Page<ExportNote> searchByExportCode(@org.springframework.data.repository.query.Param("exportCode") String exportCode, Pageable pageable);

    List<ExportNote> findByStatus(ExportStatus status);

    /**
     * Lấy danh sách ExportNote theo trạng thái VÀ chỉ những phiếu gắn với StoreOrder.
     * Mục đích: loại trừ Surplus Export (phiếu xuất thặng dư có storeOrder = null)
     * khỏi danh sách sẵn sàng giao hàng — tránh nhân viên nhầm lẫn khi tạo Delivery.
     */
    List<ExportNote> findByStatusAndStoreOrderIsNotNull(ExportStatus status);

    List<ExportNote> findByStoreOrder_OrderId(Integer orderId);
}
