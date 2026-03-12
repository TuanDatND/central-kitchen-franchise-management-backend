package com.CocOgreen.CenFra.MS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service chạy ngầm định (Scheduled) để kiểm tra và cập nhật trạng thái hết hạn
 * cho các lô hàng.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchExpirationService {

    private final ProductBatchRepository productBatchRepository;
    private final InventoryTransactionService inventoryTransactionService;

    /**
     * Cron Job tự động chạy định kỳ để dò tìm Lô Hàng đã qua Hạn sử dụng.
     * Chạy mỗi ngày lúc 00:00:00 (Nêm set @Scheduled(cron = "0 0 0 * * *"))
     * Tạm thời set fixedRate = 60000 (Chạy 1 phút 1 lần) để dễ dàng test chức năng.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void markExpiredBatches() {
        LocalDate today = LocalDate.now();
        log.info("BatchExpirationService đang khởi chạy quét các Lô hàng (Batches) hết hạn tại ngày: {}", today);

        // Quét các lô hàng đang AVAILABLE (Có sẵn trong kho) và HSD nhỏ hơn ngày hôm
        // nay
        List<ProductBatch> expiredAvailableBatches = productBatchRepository
                .findAllByStatusAndExpiryDateBefore(BatchStatus.AVAILABLE, today);

        // (Tùy chọn) Quét thêm các lô WAITING_FOR_STOCK nhưng đã quá hạn (chưa kịp nhận
        // đã hỏng)
        List<ProductBatch> expiredWaitingBatches = productBatchRepository
                .findAllByStatusAndExpiryDateBefore(BatchStatus.WAITING_FOR_STOCK, today);

        // Hợp nhất danh sách
        expiredAvailableBatches.addAll(expiredWaitingBatches);

        if (!expiredAvailableBatches.isEmpty()) {
            for (ProductBatch batch : expiredAvailableBatches) {
                log.info(
                        "- Tìm thấy lô hàng [{}] của sản phẩm [{}] hết hạn ngày {}. Đang chuyển trạng thái sang EXPIRED.",
                        batch.getBatchCode(), batch.getProduct().getProductName(), batch.getExpiryDate());

                // Nếu trong kho (AVAILABLE) vẫn còn hàng lúc hết hạn, tiến hành ghi sổ cái DISPOSAL
                int expiredQty = batch.getCurrentQuantity();
                if (expiredQty > 0 && batch.getStatus() == BatchStatus.AVAILABLE) {
                    inventoryTransactionService.logTransaction(
                            batch,
                            -expiredQty,      // Lượng tiêu huỷ - âm lượng
                            TransactionType.DISPOSAL,
                            "AUTO-EXP-" + System.currentTimeMillis(),
                            "Hệ thống tự động thanh lý dọn kho vì sản phẩm quá hạn sử dụng."
                    );                  
                }
                batch.setStatus(BatchStatus.EXPIRED);
            }

            // Lưu toàn bộ danh sách đã được đổi trạng thái xuống Database
            productBatchRepository.saveAll(expiredAvailableBatches);
            log.info("Đã cập nhật trạng thái EXPIRED thành công cho {} lô hàng.", expiredAvailableBatches.size());
        } else {
            log.info("Không có lô hàng nào hết hạn trong đợt quét này.");
        }
    }
}
