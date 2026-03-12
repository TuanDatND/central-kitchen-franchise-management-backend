package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.response.ProductBatchResponse;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.mapper.ProductBatchMapper;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý Lô Hàng (Product Batches).
 * Thuộc phạm vi Backend Dev 2 (Master Data & Inbound).
 */
@Service
@RequiredArgsConstructor
public class ProductBatchService {

    private final ProductBatchRepository productBatchRepository;
    private final ProductBatchMapper productBatchMapper;

    /**
     * Lấy danh sách lô hàng theo trạng thái (hoặc tất cả nếu status null)
     * 
     * @param status Trạng thái cần lọc (e.g. WAITING_FOR_STOCK). Nếu null thì lấy
     *               tất cả.
     * @return Danh sách ProductBatchResponse
     */
    @Transactional(readOnly = true)
    public List<ProductBatchResponse> getBatchesByStatus(BatchStatus status) {
        if (status == null) {
            // Trường hợp client không truyền status filter, trả về toàn bộ
            return productBatchRepository.findAll().stream()
                    .map(productBatchMapper::toResponse)
                    .collect(Collectors.toList());
        }

        // Trường hợp có filter theo status
        return productBatchRepository.findByStatus(status).stream()
                .map(productBatchMapper::toResponse)
                .collect(Collectors.toList());
    }

}
