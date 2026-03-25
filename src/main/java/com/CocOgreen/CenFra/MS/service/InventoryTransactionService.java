package com.CocOgreen.CenFra.MS.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.CocOgreen.CenFra.MS.dto.InventoryTransactionDto;
import com.CocOgreen.CenFra.MS.dto.PagedData;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.mapper.InventoryTransactionMapper;
import com.CocOgreen.CenFra.MS.repository.InventoryTransactionRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final UserRepository userRepository;

//    @Transactional(propagation = Propagation.MANDATORY)
//    // Propagation.MANDATORY: Đảm bảo hàm này phải chạy trong một Transaction có sẵn
    public void logTransaction(ProductBatch batch, Integer qty, TransactionType type, String refCode, String note) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setProductBatch(batch);
        tx.setQuantity(qty);
        tx.setTransactionType(type);
        tx.setReferenceCode(refCode);
        tx.setNote(note);

        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
                userRepository.findByUserName(auth.getName()).ifPresent(tx::setUser);
            }
        } catch (Exception e) {
            // Ignore in case of no security context (e.g., background jobs)
        }

        inventoryTransactionRepository.save(tx);
    }

    public PagedData<InventoryTransactionDto> findByReferenceCode(String code, Pageable pageable) {
        String fCode = (code != null) ? code.trim() : "";
        Page<InventoryTransaction> page = inventoryTransactionRepository.findByReferenceCodeContaining(fCode, pageable);
        List<InventoryTransactionDto> dtoList = page.getContent().stream().map(inventoryTransactionMapper::toDto).collect(Collectors.toList());
        return new PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    public PagedData<InventoryTransactionDto> findAll(Pageable pageable) {
        Page<InventoryTransaction> page = inventoryTransactionRepository.findAll(pageable);
        List<InventoryTransactionDto> dtoList = page.getContent().stream().map(inventoryTransactionMapper::toDto).collect(Collectors.toList());
        return new PagedData<>(dtoList, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }
}
