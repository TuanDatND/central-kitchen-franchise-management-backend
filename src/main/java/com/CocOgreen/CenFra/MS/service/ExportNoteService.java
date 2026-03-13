package com.CocOgreen.CenFra.MS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.dto.StoreOrderDTO;
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
        List<StoreOrder> approvedOrders = storeOrderRepository.findDistinctByStatusWithDetails(StoreOrderStatus.APPROVED);
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
}
