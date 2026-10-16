package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.KhachHang;
import com.fastfood.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final KhachHangService service;

    public CustomerController(KhachHangService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhachHang>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHang>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhachHang>> create(@Valid @RequestBody KhachHang customer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Customer created successfully", service.create(customer)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHang>> update(@PathVariable String id, @Valid @RequestBody KhachHang customer) {
        return ResponseEntity.ok(ApiResponse.ok("Customer updated successfully", service.update(id, customer)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Customer deleted successfully", null));
    }
}
