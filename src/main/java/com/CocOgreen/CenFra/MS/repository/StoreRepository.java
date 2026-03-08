package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.enums.StoreStatus;
import com.CocOgreen.CenFra.MS.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    Optional<Store> findByStoreName(String storeName);

    boolean existsByStoreName(String storeName);

    boolean existsByStoreNameAndStoreIdNot(String storeName, Integer storeId);

    Page<Store> findByStatus(StoreStatus status, Pageable pageable);

    long countByStatus(StoreStatus status);
}
