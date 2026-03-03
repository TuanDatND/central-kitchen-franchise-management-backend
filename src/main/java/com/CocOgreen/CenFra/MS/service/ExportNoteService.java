package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.ExportNoteDto;
import com.CocOgreen.CenFra.MS.dto.OrderDetailDTO;
import com.CocOgreen.CenFra.MS.entity.*;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.mapper.ExportNoteMapper;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepositoty;
import com.CocOgreen.CenFra.MS.repository.InventoryTransactionRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExportNoteService {
    private final ExportNoteRepositoty exportNoteRepositoty;
    private final ProductBatchRepository productBatchRepository;
    private final ExportNoteMapper exportNoteMapper;
    private final StoreOrderRepository storeOrderRepository;
    private InventoryTransactionService auditService;

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
            throw new RuntimeException("Cannot delete ExportNote which already Shipped");
        }
        exportNote.setStatus(ExportStatus.CANCEL);
    }

    @Transactional
    public ExportNoteDto createExportFromOrder(Integer storeOrderId) {
        ExportNote exportNote = new ExportNote();
        exportNote.setStatus(ExportStatus.READY);
        exportNote.setStoreOrder(storeOrderRepository.findById(storeOrderId).get());
        List<OrderDetail> storeOrdersDetail =
                storeOrderRepository.findById(storeOrderId).get().getOrderDetails();
        List<ExportItem> exportItems = new ArrayList<>();

        for (OrderDetail orderDetail : storeOrdersDetail) {
            int quantity = orderDetail.getQuantity();

            List<ProductBatch> availableBathes = productBatchRepository.findByProductAndCurrentQuantityGreaterThanOrderByExpiryDateAsc(orderDetail.getProduct(), 0);

            for (ProductBatch batch: availableBathes) {
                if(quantity <=0) break;

                int canTake = Math.min(batch.getCurrentQuantity(), quantity);
                batch.setCurrentQuantity(batch.getCurrentQuantity() - canTake);
                productBatchRepository.save(batch);
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
                        "Xuất kho cho đơn hàng: " + storeOrderId
                );
            }
            if (quantity > 0) {
                throw new RuntimeException("Kho không đủ hàng cho sản phẩm: " + orderDetail.getProduct().getProductName());
            }
        }
        exportNote.setItems(exportItems);
        return exportNoteMapper.toDto(exportNoteRepositoty.save(exportNote));
    }


}
