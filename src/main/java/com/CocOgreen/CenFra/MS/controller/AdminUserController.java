package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminUserResponse;
import com.CocOgreen.CenFra.MS.dto.CreateUserRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateUserActiveRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateUserRoleRequest;
import com.CocOgreen.CenFra.MS.service.AdminUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        return ResponseEntity.ok(adminUserService.listUsers(active, normalizedPage, normalizedSize));
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.createUser(request));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<AdminUserResponse> updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminUserService.updateRole(id, request));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<AdminUserResponse> updateActive(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserActiveRequest request) {
        return ResponseEntity.ok(adminUserService.updateActive(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        adminUserService.softDeleteUser(id);
    }
}
