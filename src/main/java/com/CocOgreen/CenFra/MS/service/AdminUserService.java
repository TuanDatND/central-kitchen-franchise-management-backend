package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.AdminUserResponse;
import com.CocOgreen.CenFra.MS.dto.CreateUserRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateUserActiveRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateUserRoleRequest;
import com.CocOgreen.CenFra.MS.entity.Role;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.repository.RoleRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(Boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "userId"));
        Page<User> users = active == null
                ? userRepository.findAll(pageable)
                : userRepository.findByIsActive(active, pageable);
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
        user.setIsActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse updateRole(Integer userId, UpdateUserRoleRequest request) {
        User user = findUser(userId);
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.setRole(role);
        return toResponse(user);
    }

    @Transactional
    public AdminUserResponse updateActive(Integer userId, UpdateUserActiveRequest request) {
        User user = findUser(userId);
        user.setIsActive(request.getIsActive());
        return toResponse(user);
    }

    @Transactional
    public void softDeleteUser(Integer userId) {
        User user = findUser(userId);
        user.setIsActive(false);
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getUserName(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getRoleName(),
                user.getIsActive()
        );
    }
}
