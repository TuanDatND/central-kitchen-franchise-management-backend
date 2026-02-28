package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.AdminDashboardResponse;
import com.CocOgreen.CenFra.MS.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> overview(
            @RequestParam(defaultValue = "5") int topStoresLimit) {
        return ResponseEntity.ok(adminDashboardService.getOverview(topStoresLimit));
    }
}
