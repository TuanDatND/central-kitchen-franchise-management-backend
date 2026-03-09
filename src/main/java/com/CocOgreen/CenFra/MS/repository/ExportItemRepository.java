package com.CocOgreen.CenFra.MS.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.CocOgreen.CenFra.MS.entity.ExportItem;

public interface ExportItemRepository extends JpaRepository<ExportItem, Integer> {
    @Override
    Page<ExportItem> findAll(Pageable pageable);

    // Lấy danh sách item theo id của phiếu xuất
    List<ExportItem> findByExportNote_ExportId(Integer exportId);

    // Báo cáo top món tiêu thụ mạnh nhất
    @Query("SELECT new com.CocOgreen.CenFra.MS.dto.response.TopProductResponse(" +
           "e.productBatch.product.productName, CAST(SUM(e.quantity) AS long)) " +
           "FROM ExportItem e " +
           "GROUP BY e.productBatch.product.productName " +
           "ORDER BY SUM(e.quantity) DESC")
    List<com.CocOgreen.CenFra.MS.dto.response.TopProductResponse> findTopConsumedProducts(Pageable pageable);

    // Báo cáo top cửa hàng nhập lớn nhất
    @Query("SELECT new com.CocOgreen.CenFra.MS.dto.response.TopStoreResponse(" +
           "e.exportNote.storeOrder.store.storeName, CAST(SUM(e.quantity) AS long)) " +
           "FROM ExportItem e " +
           "GROUP BY e.exportNote.storeOrder.store.storeName " +
           "ORDER BY SUM(e.quantity) DESC")
    List<com.CocOgreen.CenFra.MS.dto.response.TopStoreResponse> findTopImportingStores(Pageable pageable);

}
