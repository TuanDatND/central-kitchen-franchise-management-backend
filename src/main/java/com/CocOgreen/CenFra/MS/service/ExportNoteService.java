package com.CocOgreen.CenFra.MS.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.request.ManualExportRequest;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.mapper.ExportNoteMapper;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepositoty;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportNoteService {
    private final ExportNoteRepositoty exportNoteRepositoty;
    private final ProductBatchRepository productBatchRepository;
    private final ExportNoteMapper exportNoteMapper;
    private final StoreOrderRepository storeOrderRepository;
    private final InventoryTransactionService auditService;

    public List<ExportNoteDto> findAll() {
        return exportNoteRepositoty.findAll().stream().map(exportNoteMapper::toDto).collect(Collectors.toList());
    }

  public ExportNoteDto findById(Integer id) {
        ExportNote note = exportNoteRepositoty.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No ExportNote found with id: " + id));
        return exportNoteMapper.toDto(note);
    }

    @Transactional
    public ExportNote updateStatusExportNote(Integer id, ExportStatus status) {
        ExportNote exportNote = exportNoteRepositoty.findById(id).get();
        exportNote.setStatus(status);
        return exportNoteRepositoty.save(exportNote);
    }

    @Transactional
    public void deleteNote(Integer id) {
        ExportNote exportNote = exportNoteRepositoty.findById(id).get();
        if (ExportStatus.SHIPPED.equals(exportNote.getStatus())) {
            throw new com.CocOgreen.CenFra.MS.exception.InventoryOutboundException("Cannot delete ExportNote which already Shipped");
        }
        exportNote.setStatus(ExportStatus.CANCEL);
    }

    @Transactional
    public ExportNoteDto createExportFromOrder(Integer storeOrderId) {
        StoreOrder storeOrder = storeOrderRepository.findById(storeOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy StoreOrder với id: " + storeOrderId));

        ExportNote exportNote = new ExportNote();
        exportNote.setStatus(ExportStatus.READY);
        exportNote.setStoreOrder(storeOrder);
        exportNote.setExportCode("PX-" + System.currentTimeMillis());
        List<OrderDetail> storeOrdersDetail = storeOrder.getOrderDetails();
        List<ExportItem> exportItems = new ArrayList<>();

        for (OrderDetail orderDetail : storeOrdersDetail) {
            int quantity = orderDetail.getQuantity();

            List<ProductBatch> availableBathes = productBatchRepository.findByProductAndCurrentQuantityGreaterThanOrderByExpiryDateAsc(orderDetail.getProduct(), 0);

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
        return exportNoteMapper.toDto(exportNoteRepositoty.save(exportNote));
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
        exportNote = exportNoteRepositoty.save(exportNote);

        return exportNoteMapper.toDto(exportNote);
    }
}
