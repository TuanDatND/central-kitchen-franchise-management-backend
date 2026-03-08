package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.AdminUserResponse;
import com.CocOgreen.CenFra.MS.dto.CreateUserRequest;
import com.CocOgreen.CenFra.MS.entity.Role;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.UserStatus;
import com.CocOgreen.CenFra.MS.exception.ResourceNotFoundException;
import com.CocOgreen.CenFra.MS.repository.RoleRepository;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(Integer storeId, UserStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "userId"));
        Page<User> users;
        if (storeId != null) {
            users = status == null
                    ? userRepository.findByStore_StoreId(storeId, pageable)
                    : userRepository.findByStore_StoreIdAndStatus(storeId, status, pageable);
        } else {
            users = status == null
                    ? userRepository.findAll(pageable)
                    : userRepository.findByStatus(status, pageable);
        }
        return users.map(this::toResponse);
    }

    @Transactional
    public AdminUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUserName(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        User user = new User();
        user.setUserName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setStore(resolveAssignableStore(role.getRoleName(), request.getStoreId()));
        user.setStatus(request.getStatus() == null ? UserStatus.ACTIVE : request.getStatus());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse updateUser(Integer userId, com.CocOgreen.CenFra.MS.dto.UpdateUserRequest request) {
        User user = findUser(userId);
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            Role role = roleRepository.findByRoleName(request.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole(role);
        }
        RoleName effectiveRole = user.getRole().getRoleName();
        if (request.getStoreId() != null || effectiveRole != RoleName.FRANCHISE_STORE_STAFF) {
            user.setStore(resolveAssignableStore(effectiveRole, request.getStoreId()));
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse softDeleteUser(Integer userId) {
        User user = findUser(userId);
        user.setStatus(UserStatus.INACTIVE);
        return toResponse(user);
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Store resolveAssignableStore(RoleName roleName, Integer storeId) {
        if (roleName == RoleName.FRANCHISE_STORE_STAFF) {
            if (storeId == null) {
                throw new IllegalArgumentException("storeId là bắt buộc với nhân viên cửa hàng");
            }
            return storeRepository.findById(storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cửa hàng"));
        }
        return null;
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getUserName(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getStore() == null ? null : user.getStore().getStoreId(),
                user.getStore() == null ? null : user.getStore().getStoreName(),
                user.getStatus());
    }
}
