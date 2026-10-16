package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.DanhMuc;
import com.fastfood.service.DanhMucService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final DanhMucService service;

    public CategoryController(DanhMucService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DanhMuc>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMuc>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DanhMuc>> create(@Valid @RequestBody DanhMuc category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", service.create(category)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DanhMuc>> update(@PathVariable String id, @Valid @RequestBody DanhMuc category) {
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", service.update(id, category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted successfully", null));
    }
}
