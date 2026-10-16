package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.dto.DashboardStatsDTO;
import com.fastfood.service.FastFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final FastFoodService fastFoodService;

    public DashboardController(FastFoodService fastFoodService) {
        this.fastFoodService = fastFoodService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(fastFoodService.getDashboardStats()));
    }
}
