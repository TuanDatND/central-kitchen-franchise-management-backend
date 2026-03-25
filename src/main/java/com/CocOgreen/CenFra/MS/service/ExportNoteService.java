package com.CocOgreen.CenFra.MS.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
import com.CocOgreen.CenFra.MS.dto.response.ExportPreviewResponse;
import com.CocOgreen.CenFra.MS.dto.request.ManualExportRequest;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.mapper.ExportNoteMapper;
import com.CocOgreen.CenFra.MS.mapper.StoreOrderMapper;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class  ExportNoteService {
    private final ExportNoteRepository exportNoteRepository;
    private final ProductBatchRepository productBatchRepository;
    private final ExportNoteMapper exportNoteMapper;
    private final StoreOrderRepository storeOrderRepository;
    private final InventoryTransactionService auditService;
    private final StoreOrderMapper storeOrderMapper;
    private final UserRepository userRepository;

    public PagedData<ExportNoteDto> findAll(Pageable pageable) {
        Page<ExportNote> page = exportNoteRepository.findAll(pageable);
        List<ExportNoteDto> dtoList = page.getContent().stream().map(exportNoteMapper::toDto).collect(Collectors.toList());
        return new PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    public PagedData<ExportNoteDto> findByExportCode(String exportCode, Pageable pageable) {
        String searchKeyword = (exportCode != null) ? exportCode.trim() : "";
        Page<ExportNote> page = exportNoteRepository.searchByExportCode(searchKeyword, pageable);
        List<ExportNoteDto> dtoList = page.getContent().stream().map(exportNoteMapper::toDto).collect(Collectors.toList());
        return new PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    public ExportNoteDto findById(Integer id) {
        ExportNote note = exportNoteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No ExportNote found with id: " + id));
        return exportNoteMapper.toDto(note);
    }

    /**
     * Lấy danh sách các đơn hàng (StoreOrder) đã được duyệt (APPROVED)
     * và có đủ số lượng tồn kho (trong các ProductBatch AVAILABLE) để xuất.
     */
    @Transactional
    public List<StoreOrderDTO> getReadyStoreOrders() {
        List<StoreOrder> approvedOrders = storeOrderRepository.findDistinctByStatusWithDetails(StoreOrderStatus.CONSOLIDATED);
        List<StoreOrder> readyOrders = new ArrayList<>();

        for (StoreOrder order : approvedOrders) {
            boolean isReady = true;
            for (OrderDetail detail : order.getOrderDetails()) {
                int requiredQty = detail.getQuantity();

                List<ProductBatch> availableBatches = productBatchRepository.findAvailableProducts(detail.getProduct(), 0);
                long totalAvailable = availableBatches.stream()
                        .mapToLong(ProductBatch::getCurrentQuantity)
                        .sum();

                if (totalAvailable < requiredQty) {
                    isReady = false;
                    break; // Không đủ hàng cho 1 sản phẩm -> Đơn hàng chưa xuất được
                }
            }
            if (isReady) {
                readyOrders.add(order);
            }
        }

        return readyOrders.stream().map(storeOrderMapper::toDTO).collect(Collectors.toList());
    }


    @Transactional
    public void updateStatusExportNote(Integer id, ExportStatus status) {
        ExportNote exportNote = exportNoteRepository.findById(id).get();
        exportNote.setStatus(status);
        exportNoteRepository.save(exportNote);
    }

    @Transactional
    public void deleteNote(Integer id) {
        ExportNote exportNote = exportNoteRepository.findById(id).get();
        if (ExportStatus.SHIPPED.equals(exportNote.getStatus())) {
            throw new com.CocOgreen.CenFra.MS.exception.InventoryOutboundException("Cannot delete ExportNote which already Shipped");
        }
        exportNote.setStatus(ExportStatus.CANCEL);
    }

   

    /**
     * Tạo phiếu xuất tự động từ danh sách đơn hàng (FEFO)
     * Lặp qua từng đơn hàng và xử lý xuất lô phù hợp.
     * Cả quá trình nằm trong một transaction để đảm bảo toàn vẹn dữ liệu.
     */
    @Transactional
    public List<ExportNoteDto> createExportFromOrder(List<Integer> storeOrderIds) {
        if (storeOrderIds == null || storeOrderIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách StoreOrder ID không được rỗng.");
        }

        List<ExportNoteDto> result = new ArrayList<>();

        for (Integer storeOrderId : storeOrderIds) {
            StoreOrder storeOrder = storeOrderRepository.findById(storeOrderId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy StoreOrder với id: " + storeOrderId));

            ExportNote exportNote = new ExportNote();
            exportNote.setStatus(ExportStatus.READY);
            exportNote.setStoreOrder(storeOrder);
            exportNote.setExportCode("PX-" + System.currentTimeMillis() + "-" + storeOrderId);
            
            try {
                org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                    userRepository.findByUserName(auth.getName()).ifPresent(exportNote::setCreatedBy);
                }
            } catch (Exception e) {
                // Ignore
            }

            List<OrderDetail> storeOrdersDetail = storeOrder.getOrderDetails();
            List<ExportItem> exportItems = new ArrayList<>();

            for (OrderDetail orderDetail : storeOrdersDetail) {
                int quantity = orderDetail.getQuantity();

                List<ProductBatch> availableBathes = productBatchRepository.findAvailableProducts(orderDetail.getProduct(), 0);

                for (ProductBatch batch : availableBathes) {
                    if (quantity <= 0) break;

                    int canTake = Math.min(batch.getCurrentQuantity(), quantity);
                    batch.setCurrentQuantity(batch.getCurrentQuantity() - canTake);


                    ExportItem item = new ExportItem();
                    item.setExportNote(exportNote);
                    item.setProductBatch(batch);
                    item.setQuantity(canTake);
                    exportItems.add(item);

                    quantity -= canTake;

                    auditService.logTransaction(
                            batch,
                            -canTake,
                            TransactionType.EXPORT,
                            exportNote.getExportCode(),
                            "Xuất kho cho đơn hàng: " + storeOrder.getStore()
                    );
                }
                if (quantity > 0) {
                    throw new com.CocOgreen.CenFra.MS.exception.InventoryOutboundException(String.format(
                            "Kho không đủ hàng cho sản phẩm: %s. Còn thiếu: %d %s",
                            orderDetail.getProduct().getProductName(),
                            quantity,
                            orderDetail.getProduct().getUnit()
                    ));
                }
            }
            exportNote.setItems(exportItems);
            storeOrder.setStatus(StoreOrderStatus.IN_TRANSIT);
            result.add(exportNoteMapper.toDto(exportNoteRepository.save(exportNote)));
        }
        return result;
    }

    @Transactional
    public ExportNoteDto createExportFromManualBatches(ManualExportRequest request) {
        StoreOrder storeOrder = storeOrderRepository.findById(request.getStoreOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy Store Order ID: " + request.getStoreOrderId()));

        ExportNote exportNote = new ExportNote();
        exportNote.setExportCode("PX-" + System.currentTimeMillis());
        exportNote.setStatus(ExportStatus.READY);
        exportNote.setStoreOrder(storeOrder);

        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                userRepository.findByUserName(auth.getName()).ifPresent(exportNote::setCreatedBy);
            }
        } catch (Exception e) {
            // Ignore
        }

        List<ExportItem> exportItems = new ArrayList<>();

        for (ManualExportRequest.Item selection : request.getSelectedBatches()) {
            ProductBatch batch = productBatchRepository.findById(selection.getBatchId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy ProductBatch ID: " + selection.getBatchId()));

            int manualQuantity = selection.getQuantity();

            if (batch.getCurrentQuantity() < manualQuantity) {
                throw new com.CocOgreen.CenFra.MS.exception.InventoryOutboundException("Lô hàng " + batch.getBatchCode()
                        + " không đủ số lượng tồn kho. (Kho hiện tại: " + batch.getCurrentQuantity() + ")");
            }

            batch.setCurrentQuantity(batch.getCurrentQuantity() - manualQuantity);

            ExportItem item = new ExportItem();
            item.setExportNote(exportNote);
            item.setProductBatch(batch);
            item.setQuantity(manualQuantity);
            exportItems.add(item);

            auditService.logTransaction(
                    batch,
                    -manualQuantity,
                    TransactionType.EXPORT,
                    exportNote.getExportCode(),
                    "Thao tác xuất kho manual cho Store Order: " + storeOrder.getOrderCode());
        }

        exportNote.setItems(exportItems);
        exportNote = exportNoteRepository.save(exportNote);

        return exportNoteMapper.toDto(exportNote);
    }

    /**
     * XEM TRƯỚC (PREVIEW) kế hoạch xuất kho theo thuật toán FEFO.
     *
     * Phương thức này KHÔNG lưu bất kỳ dữ liệu nào vào database.
     * Mục đích: cho phép người dùng (SUPPLY_COORDINATOR) xem trước
     * hệ thống dự định lấy từ lô batch nào, bao nhiêu, trước khi
     * chính thức xác nhận tạo phiếu xuất.
     *
     * Quy tắc FEFO (First Expired First Out):
     * - Ưu tiên lô có ngày hết hạn sớm nhất trước.
     * - Nếu kho thiếu: đánh dấu shortfall (số lượng còn thiếu) nhưng vẫn trả về kết quả.
     *
     * @param storeOrderIds Danh sách ID các đơn hàng cần xem trước
     * @return Danh sách ExportPreviewResponse chứa thông tin phân bổ lô hàng
     */
    @Transactional
    public List<ExportPreviewResponse> previewExportFromOrder(List<Integer> storeOrderIds) {
        if (storeOrderIds == null || storeOrderIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách StoreOrder ID không được rỗng.");
        }

        List<ExportPreviewResponse> previewList = new ArrayList<>();

        // =================================================================
        // Kho ảo (virtual stock): theo dõi số lượng còn lại của từng batch
        // XUYÊN SUỐT tất cả các order trong cùng 1 lần preview.
        // Key   = batchId
        // Value = số lượng còn lại trong lô (sau khi đã phân bổ cho các order trước)
        // =================================================================
        Map<Integer, Integer> virtualStock = new HashMap<>();

        for (Integer storeOrderId : storeOrderIds) {
            // --- Lấy thông tin đơn hàng ---
            StoreOrder storeOrder = storeOrderRepository.findById(storeOrderId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Không tìm thấy StoreOrder với id: " + storeOrderId));

            // --- Xây dựng danh sách sản phẩm preview ---
            List<ExportPreviewResponse.ProductPreviewItem> productPreviews = new ArrayList<>();
            boolean orderCanFulfill = true;

            for (OrderDetail orderDetail : storeOrder.getOrderDetails()) {
                int requiredQuantity = orderDetail.getQuantity();
                int remaining = requiredQuantity; // Số lượng còn cần lấy

                // Lấy danh sách lô AVAILABLE của sản phẩm, sắp xếp theo ngày hết hạn (FEFO)
                List<ProductBatch> availableBatches =
                        productBatchRepository.findAvailableProducts(orderDetail.getProduct(), 0);

                List<ExportPreviewResponse.BatchAllocation> allocations = new ArrayList<>();

                // ---  FEFO: duyệt từng lô, phân bổ số lượng ---
                for (ProductBatch batch : availableBatches) {
                    if (remaining <= 0) break;

                    // Lấy tồn kho ảo của lô này.
                    // Nếu chưa có trong map (lần đầu gặp batch này) → lấy từ DB làm giá trị khởi tạo.
                    // Nếu đã có trong map (order trước đã dùng) → lấy giá trị đã giảm.
                    int virtualRemaining = virtualStock.getOrDefault(
                            batch.getBatchId(), batch.getCurrentQuantity());

                    // Bỏ qua lô này nếu tồn kho ảo đã hết (do order trước đã lấy hết)
                    if (virtualRemaining <= 0) continue;

                    // Số lượng sẽ lấy từ lô này (không vượt quá tồn kho ảo của lô)
                    int allocate = Math.min(virtualRemaining, remaining);

                    allocations.add(ExportPreviewResponse.BatchAllocation.builder()
                            .batchId(batch.getBatchId())
                            .batchCode(batch.getBatchCode())
                            .expiryDate(batch.getExpiryDate())
                            .manufacturingDate(batch.getManufacturingDate())
                            .currentStock(virtualRemaining) // Tồn kho ảo tại thời điểm phân bổ
                            .allocatedQuantity(allocate)    // Sẽ trừ bao nhiêu
                            .build());

                    // Cập nhật kho ảo: trừ đi lượng vừa phân bổ cho order này
                    virtualStock.put(batch.getBatchId(), virtualRemaining - allocate);

                    remaining -= allocate;
                }

                // Tổng lượng có thể đáp ứng = yêu cầu - shortfall
                int shortfall = remaining; // Nếu > 0: kho thiếu hàng
                int fulfillable = requiredQuantity - shortfall;

                if (shortfall > 0) {
                    orderCanFulfill = false; // Đơn hàng không thể xuất đủ
                }

                productPreviews.add(ExportPreviewResponse.ProductPreviewItem.builder()
                        .productId(orderDetail.getProduct().getProductId())
                        .productName(orderDetail.getProduct().getProductName())
                        // Lấy tên đơn vị từ Unit entity (null-safe)
                        .unit(orderDetail.getProduct().getUnit() != null
                                ? orderDetail.getProduct().getUnit().getUnitName()
                                : "N/A")
                        .requiredQuantity(requiredQuantity)
                        .fulfillableQuantity(fulfillable)
                        .shortfall(shortfall)
                        .batchAllocations(allocations)
                        .build());
            }

            // --- Tổng hợp kết quả preview cho đơn hàng ---
            previewList.add(ExportPreviewResponse.builder()
                    .storeOrderId(storeOrder.getOrderId())
                    .orderCode(storeOrder.getOrderCode())
                    .storeName(storeOrder.getStore() != null ? storeOrder.getStore().getStoreName() : "N/A")
                    .canFulfill(orderCanFulfill)
                    .products(productPreviews)
                    .build());
        }

        return previewList;
    }
}

