package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.TaiKhoan;
import com.fastfood.service.TaiKhoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final TaiKhoanService service;

    public AccountController(TaiKhoanService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<TaiKhoan>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaiKhoan>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/by-employee/{maNV}")
    public ResponseEntity<ApiResponse<TaiKhoan>> getByEmployee(@PathVariable String maNV) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByEmployeeId(maNV)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaiKhoan>> create(@Valid @RequestBody TaiKhoan account) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Account created successfully", service.create(account)));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<TaiKhoan>> lockAccount(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Account locked", service.lockAccount(id)));
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<TaiKhoan>> unlockAccount(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Account unlocked", service.unlockAccount(id)));
    }

    @PutMapping("/{id}/login")
    public ResponseEntity<ApiResponse<TaiKhoan>> recordLogin(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Login recorded", service.recordLogin(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Account deleted successfully", null));
    }
}
