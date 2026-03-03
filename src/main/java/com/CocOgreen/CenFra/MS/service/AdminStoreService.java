package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.AdminStoreResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreInfoRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreManagerRequest;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<AdminStoreResponse> listStores(Boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "storeId"));
        Page<Store> stores = active == null
                ? storeRepository.findAll(pageable)
                : storeRepository.findByIsActive(active, pageable);
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

        User manager = resolveStoreManager(request.getManagerUserId());

        Store store = new Store();
        store.setStoreName(normalizedStoreName);
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());
        store.setManager(manager);
        store.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());

        return toResponse(storeRepository.save(store));
    }

    @Transactional
    public AdminStoreResponse updateStoreInfo(Integer storeId, UpdateStoreInfoRequest request) {
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

        return toResponse(store);
    }

    @Transactional
    public AdminStoreResponse updateManager(Integer storeId, UpdateStoreManagerRequest request) {
        Store store = findStore(storeId);
        User manager = resolveStoreManager(request.getManagerUserId());
        store.setManager(manager);
        return toResponse(store);
    }

    @Transactional
    public AdminStoreResponse updateActive(Integer storeId, boolean isActive) {
        Store store = findStore(storeId);
        store.setIsActive(isActive);
        return toResponse(store);
    }

    private Store findStore(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    private User resolveStoreManager(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Manager user not found"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("Manager user is inactive");
        }
        if (user.getRole() == null || user.getRole().getRoleName() != RoleName.FRANCHISE_STORE_STAFF) {
            throw new IllegalArgumentException("User must have FRANCHISE_STORE_STAFF role");
        }
        return user;
    }

    private AdminStoreResponse toResponse(Store store) {
        User manager = store.getManager();
        return new AdminStoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getAddress(),
                store.getPhone(),
                store.getIsActive(),
                manager == null ? null : manager.getUserId(),
                manager == null ? null : manager.getUserName(),
                manager == null ? null : manager.getFullName()
        );
    }
}
