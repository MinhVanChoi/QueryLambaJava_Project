package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.KhuyenMai;
import com.fastfood.service.KhuyenMaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final KhuyenMaiService service;

    public PromotionController(KhuyenMaiService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhuyenMai>>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<KhuyenMai> list = activeOnly ? service.getActivePromotions() : service.getAll();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping("/{id}/calc-discount")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateDiscount(
            @PathVariable String id,
            @RequestBody Map<String, BigDecimal> body
    ) {
        BigDecimal subtotal = body.getOrDefault("subtotal", BigDecimal.ZERO);
        BigDecimal discount = service.calculateDiscount(id, subtotal);
        return ResponseEntity.ok(ApiResponse.ok("Discount calculated", discount));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhuyenMai>> create(@Valid @RequestBody KhuyenMai promotion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Promotion created successfully", service.create(promotion)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhuyenMai>> update(@PathVariable String id, @Valid @RequestBody KhuyenMai promotion) {
        return ResponseEntity.ok(ApiResponse.ok("Promotion updated successfully", service.update(id, promotion)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Promotion deleted successfully", null));
    }
}
