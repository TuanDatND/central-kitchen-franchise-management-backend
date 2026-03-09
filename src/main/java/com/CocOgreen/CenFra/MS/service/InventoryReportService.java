package com.CocOgreen.CenFra.MS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CocOgreen.CenFra.MS.dto.response.NearExpiryBatchResponse;
import com.CocOgreen.CenFra.MS.dto.response.StockSummaryResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopProductResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopStoreResponse;
import com.CocOgreen.CenFra.MS.repository.ExportItemRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryReportService {

    private final ProductBatchRepository productBatchRepository;
    private final ExportItemRepository exportItemRepository;

    /**
     * Báo cáo tồn kho trung tâm
     * Quyền: MANAGER, SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF
     */
    @Transactional(readOnly = true)
    public List<StockSummaryResponse> getStockSummary() {
        return productBatchRepository.findStockSummary();
    }

    /**
     * Báo cáo lô hàng sắp hết hạn
     * Quyền: MANAGER, SUPPLY_COORDINATOR
     * Mặc định lấy các lô hàng sẽ hết hạn trong vòng `daysThreshold` ngày tới.
     */
    @Transactional(readOnly = true)
    public List<NearExpiryBatchResponse> getNearExpiryBatches(int daysThreshold) {
        LocalDate targetDate = LocalDate.now().plusDays(daysThreshold);
        return productBatchRepository.findNearExpiryBatches(targetDate);
    }

    /**
     * Thống kê món tiêu thụ mạnh nhất
     * Quyền: MANAGER, ADMIN
     */
    @Transactional(readOnly = true)
    public List<TopProductResponse> getTopConsumedProducts(int limit) {
        return exportItemRepository.findTopConsumedProducts(PageRequest.of(0, limit));
    }

    /**
     * Thống kê cửa hàng nhập lớn nhất
     * Quyền: MANAGER, ADMIN
     */
    @Transactional(readOnly = true)
    public List<TopStoreResponse> getTopImportingStores(int limit) {
        return exportItemRepository.findTopImportingStores(PageRequest.of(0, limit));
    }
}
