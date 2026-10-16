package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.TonKho;
import com.fastfood.service.TonKhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final TonKhoService service;

    public InventoryController(TonKhoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TonKho>>> getAll(
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false, defaultValue = "false") boolean lowStock,
            @RequestParam(required = false, defaultValue = "10") int threshold
    ) {
        List<TonKho> list;
        if (lowStock) {
            list = service.getLowStock(threshold);
        } else if (branchId != null && !branchId.isBlank()) {
            list = service.getByBranch(branchId);
        } else {
            list = service.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TonKho>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<TonKho>> updateStock(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer qty = body.get("soLuong");
        if (qty == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Field 'soLuong' is required"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", service.updateStock(id, qty)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TonKho>> create(@Valid @RequestBody TonKho inventory) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Inventory record created successfully", service.create(inventory)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Inventory record deleted successfully", null));
    }
}
