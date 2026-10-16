package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.ThanhToan;
import com.fastfood.enums.TrangThaiThanhToan;
import com.fastfood.service.ThanhToanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ThanhToanService service;

    public PaymentController(ThanhToanService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThanhToan>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThanhToan>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/by-order/{maDH}")
    public ResponseEntity<ApiResponse<List<ThanhToan>>> getByOrderId(@PathVariable Long maDH) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByOrderId(maDH)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ThanhToan>> create(@Valid @RequestBody ThanhToan payment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment recorded successfully", service.create(payment)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ThanhToan>> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Status field is required"));
        }
        TrangThaiThanhToan next = TrangThaiThanhToan.valueOf(statusStr.toUpperCase().trim());
        return ResponseEntity.ok(ApiResponse.ok("Payment status updated", service.updateStatus(id, next)));
    }
}
