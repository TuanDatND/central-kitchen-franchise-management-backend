package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.repository.InventoryTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;

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
}
