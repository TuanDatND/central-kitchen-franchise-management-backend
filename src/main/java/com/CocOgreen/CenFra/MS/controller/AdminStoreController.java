package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminStoreResponse;
import com.CocOgreen.CenFra.MS.dto.CreateStoreRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreActiveRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreInfoRequest;
import com.CocOgreen.CenFra.MS.dto.UpdateStoreManagerRequest;
import com.CocOgreen.CenFra.MS.service.AdminStoreService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStoreController {
    private final AdminStoreService adminStoreService;

    @GetMapping
    public ResponseEntity<Page<AdminStoreResponse>> listStores(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int normalizedPage = Math.max(page, 0);
        return ResponseEntity.ok(adminStoreService.listStores(active, normalizedPage, normalizedSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminStoreResponse> getStore(@PathVariable Integer id) {
        return ResponseEntity.ok(adminStoreService.getStore(id));
    }

    @PostMapping
    public ResponseEntity<AdminStoreResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminStoreService.createStore(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminStoreResponse> updateStoreInfo(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreInfoRequest request) {
        return ResponseEntity.ok(adminStoreService.updateStoreInfo(id, request));
    }

    @PatchMapping("/{id}/manager")
    public ResponseEntity<AdminStoreResponse> updateManager(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreManagerRequest request) {
        return ResponseEntity.ok(adminStoreService.updateManager(id, request));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<AdminStoreResponse> updateActive(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateStoreActiveRequest request) {
        return ResponseEntity.ok(adminStoreService.updateActive(id, request.getIsActive()));
    }
}
