package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.InventoryTransactionDto;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.mapper.InventoryTransactionMapper;
import com.CocOgreen.CenFra.MS.repository.InventoryTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;

//    @Transactional(propagation = Propagation.MANDATORY)
//    // Propagation.MANDATORY: Đảm bảo hàm này phải chạy trong một Transaction có sẵn
    public void logTransaction(ProductBatch batch, Integer qty, TransactionType type, String refCode, String note) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setProductBatch(batch);
        tx.setQuantity(qty);
        tx.setTransactionType(type);
        tx.setReferenceCode(refCode);
        tx.setNote(note);
        inventoryTransactionRepository.save(tx);
    }

    public Page<InventoryTransactionDto> findByReferenceCode(String code, Pageable pageable) {
        String fCode = (code != null) ? code.trim() : "";
        return inventoryTransactionRepository.findByReferenceCodeContaining(fCode, pageable).map(inventoryTransactionMapper::toDto);
    }

    public Page<InventoryTransactionDto> findAll(Pageable pageable) {
        return inventoryTransactionRepository.findAll(pageable).map(inventoryTransactionMapper::toDto);
    }

}
