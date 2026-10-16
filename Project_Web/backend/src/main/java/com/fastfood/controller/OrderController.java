package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.dto.OrderCreateRequest;
import com.fastfood.dto.OrderDetailResponseDTO;
import com.fastfood.entity.DonHang;
import com.fastfood.enums.TrangThaiDonHang;
import com.fastfood.service.DonHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final DonHangService service;

    public OrderController(DonHangService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DonHang>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonHang>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<OrderDetailResponseDTO>> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getOrderDetail(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponseDTO>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderDetailResponseDTO created = service.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order created successfully", created));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DonHang>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Status field is required"));
        }
        TrangThaiDonHang next = TrangThaiDonHang.valueOf(statusStr.toUpperCase().trim());
        return ResponseEntity.ok(ApiResponse.ok("Status updated", service.updateStatus(id, next)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Order deleted successfully", null));
    }
}
