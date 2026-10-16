package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.ChiNhanh;
import com.fastfood.service.ChiNhanhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final ChiNhanhService service;

    public BranchController(ChiNhanhService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChiNhanh>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChiNhanh>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChiNhanh>> create(@Valid @RequestBody ChiNhanh branch) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Branch created successfully", service.create(branch)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChiNhanh>> update(@PathVariable String id, @Valid @RequestBody ChiNhanh branch) {
        return ResponseEntity.ok(ApiResponse.ok("Branch updated successfully", service.update(id, branch)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Branch deleted successfully", null));
    }
}
