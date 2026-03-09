package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.AdminStoreResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreRequest;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.enums.StoreStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStoreService {
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public Page<AdminStoreResponse> listStores(StoreStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "storeId"));
        Page<Store> stores = status == null
                ? storeRepository.findAll(pageable)
                : storeRepository.findByStatus(status, pageable);
        return stores.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AdminStoreResponse getStore(Integer storeId) {
        return toResponse(findStore(storeId));
    }

    @Transactional
    public AdminStoreResponse createStore(CreateStoreRequest request) {
        String normalizedStoreName = request.getStoreName().trim();
        if (storeRepository.existsByStoreName(normalizedStoreName)) {
            throw new IllegalArgumentException("Store name already exists");
        }

        Store store = new Store();
        store.setStoreName(normalizedStoreName);
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());
        store.setStatus(request.getStatus() == null ? StoreStatus.ACTIVE : request.getStatus());

        return toResponse(storeRepository.save(store));
    }

    @Transactional
    public AdminStoreResponse updateStore(Integer storeId, UpdateStoreRequest request) {
        Store store = findStore(storeId);

        if (request.getStoreName() != null && !request.getStoreName().isBlank()) {
            String normalizedStoreName = request.getStoreName().trim();
            if (storeRepository.existsByStoreNameAndStoreIdNot(normalizedStoreName, storeId)) {
                throw new IllegalArgumentException("Store name already exists");
            }
            store.setStoreName(normalizedStoreName);
        }
        if (request.getAddress() != null) {
            store.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            store.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            store.setStatus(request.getStatus());
        }
        return toResponse(store);
    }

    @Transactional
    public AdminStoreResponse softDeleteStore(Integer storeId) {
        Store store = findStore(storeId);
        store.setStatus(StoreStatus.INACTIVE);
        return toResponse(store);
    }

    private Store findStore(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng"));
    }

    private AdminStoreResponse toResponse(Store store) {
        return new AdminStoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getAddress(),
                store.getPhone(),
                store.getStatus());
    }
}
