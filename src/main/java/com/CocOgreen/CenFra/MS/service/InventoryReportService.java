package com.CocOgreen.CenFra.MS.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.CocOgreen.CenFra.MS.enums.InventoryStockStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.response.NearExpiryBatchResponse;
import com.CocOgreen.CenFra.MS.dto.response.ProductBatchResponse;
import com.CocOgreen.CenFra.MS.dto.response.StockSummaryResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopProductResponse;
import com.CocOgreen.CenFra.MS.dto.response.TopStoreResponse;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.repository.ExportItemRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'CENTRAL_KITCHEN_STAFF','SUPPLY_COORDINATOR')")
public class InventoryReportService {

    private final ProductBatchRepository productBatchRepository;
    private final ExportItemRepository exportItemRepository;

    /**
     * Báo cáo tồn kho trung tâm
     * Quyền: MANAGER, SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF
     */
    @Transactional(readOnly = true)
    public PagedData<StockSummaryResponse> getStockSummary() {
        // Lấy tất cả các lô hàng đang AVAILABLE
        List<ProductBatch> availableBatches = productBatchRepository.findByStatus(com.CocOgreen.CenFra.MS.enums.BatchStatus.AVAILABLE);

        // Gom nhóm theo Product
        Map<Product, List<ProductBatch>> grouped = availableBatches.stream()
                .collect(Collectors.groupingBy(ProductBatch::getProduct));

        LocalDate now = LocalDate.now();
        List<StockSummaryResponse> list = new ArrayList<>();

        for (Map.Entry<Product, List<ProductBatch>> entry : grouped.entrySet()) {
            Product product = entry.getKey();
            List<ProductBatch> batches = entry.getValue();

            // Sắp xếp các batch theo hạn sử dụng tăng dần (FEFO)
            batches.sort(Comparator.comparing(ProductBatch::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder())));

            long totalStock = batches.stream().mapToLong(ProductBatch::getCurrentQuantity).sum();

            // Tính toán warning dựa trên số lượng lô hàng sắp/đã hết hạn
            int expiredCount = 0;
            int nearExpiryCount = 0;
            LocalDate earliestExpiry = null;
            Integer representBatchId = null;

            if (!batches.isEmpty()) {
                ProductBatch earliestBatch = batches.get(0);
                earliestExpiry = earliestBatch.getExpiryDate();
                representBatchId = earliestBatch.getBatchId();
            }

            for (ProductBatch batch : batches) {
                if (batch.getExpiryDate() != null) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(now, batch.getExpiryDate());
                    if (days < 0) {
                        expiredCount++;
                    } else if (days <= 7) {
                        nearExpiryCount++;
                    }
                }
            }

            String warning = "Bình thường";
            InventoryStockStatus status = InventoryStockStatus.AVAILABLE;
            if (expiredCount > 0 && nearExpiryCount > 0) {
                warning = "Có " + expiredCount + " lô đã hết hạn, " + nearExpiryCount + " lô sắp hết hạn";
                status = InventoryStockStatus.WARNING;
            } else if (expiredCount > 0) {
                warning = "Có " + expiredCount + " lô đã hết hạn";
                status = InventoryStockStatus.EXPIRED;
            } else if (nearExpiryCount > 0) {
                warning = "Có " + nearExpiryCount + " lô sắp hết hạn";
                status = InventoryStockStatus.EXPIRING_SOON;
            }

            // Map danh sách lô hàng sang ProductBatchResponse
            List<ProductBatchResponse> batchResponses = batches.stream().map(b -> {
                ProductBatchResponse br = new ProductBatchResponse();
                br.setBatchId(b.getBatchId());
                br.setBatchCode(b.getBatchCode());
                br.setProductName(product.getProductName());
                br.setCurrentQuantity(b.getCurrentQuantity());
                br.setInitialQuantity(b.getInitialQuantity());
                br.setUnitName(product.getUnit().getUnitName());
                br.setExpiryDate(b.getExpiryDate());
                br.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
                return br;
            }).collect(Collectors.toList());

            StockSummaryResponse response = new StockSummaryResponse();
            response.setProductName(product.getProductName());
            response.setProductId(product.getProductId());
            response.setBatchId(representBatchId); 
            response.setExpiryDate(earliestExpiry);
            response.setUnit(product.getUnit().getUnitName());
            response.setTotalStock(totalStock);
            response.setWarning(warning);
            response.setProductBatch(batchResponses);
            response.setStatus(status);
            list.add(response);
        }

        // Sắp xếp danh sách tổng hợp theo tên sản phẩm
        list.sort(Comparator.comparing(StockSummaryResponse::getProductName));
        return new PagedData<>(list, 0, Math.max(list.size(), 1), list.size(), 1, true, true);
    }

    /**
     * Báo cáo lô hàng sắp hết hạn
     * Quyền: MANAGER, SUPPLY_COORDINATOR
     * Mặc định lấy các lô hàng sẽ hết hạn trong vòng `daysThreshold` ngày tới.
     */
    @Transactional(readOnly = true)
    public PagedData<NearExpiryBatchResponse> getNearExpiryBatches(int daysThreshold) {
        LocalDate targetDate = LocalDate.now().plusDays(daysThreshold);
        List<NearExpiryBatchResponse> list = productBatchRepository.findNearExpiryBatches(targetDate);
        return new PagedData<>(list, 0, Math.max(list.size(), 1), list.size(), 1, true, true);
    }

    /**
     * Thống kê món tiêu thụ mạnh nhất
     * Quyền: MANAGER, ADMIN
     */
    @Transactional(readOnly = true)
    public PagedData<TopProductResponse> getTopConsumedProducts(int limit) {
        List<TopProductResponse> list = exportItemRepository.findTopConsumedProducts(PageRequest.of(0, limit));
        return new PagedData<>(list, 0, limit, list.size(), 1, true, true);
    }

    /**
     * Thống kê cửa hàng nhập lớn nhất
     * Quyền: MANAGER, ADMIN
     */
    @Transactional(readOnly = true)
    public PagedData<TopStoreResponse> getTopImportingStores(int limit) {
        List<TopStoreResponse> list = exportItemRepository.findTopImportingStores(PageRequest.of(0, limit));
        return new PagedData<>(list, 0, limit, list.size(), 1, true, true);
    }
}
