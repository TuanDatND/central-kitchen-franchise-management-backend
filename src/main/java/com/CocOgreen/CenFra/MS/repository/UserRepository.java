package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.enums.UserStatus;
import com.CocOgreen.CenFra.MS.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    Page<User> findByStore_StoreId(Integer storeId, Pageable pageable);

    Page<User> findByStore_StoreIdAndStatus(Integer storeId, UserStatus status, Pageable pageable);

    long countByStatus(UserStatus status);

}
