package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.NhanVien;
import com.fastfood.service.NhanVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final NhanVienService service;

    public EmployeeController(NhanVienService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NhanVien>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String branchId
    ) {
        List<NhanVien> list;
        if (keyword != null && !keyword.isBlank()) {
            list = service.searchByName(keyword);
        } else if (branchId != null && !branchId.isBlank()) {
            list = service.getByBranch(branchId);
        } else {
            list = service.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NhanVien>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NhanVien>> create(@Valid @RequestBody NhanVien employee) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee created successfully", service.create(employee)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NhanVien>> update(@PathVariable String id, @Valid @RequestBody NhanVien employee) {
        return ResponseEntity.ok(ApiResponse.ok("Employee updated successfully", service.update(id, employee)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deleted successfully", null));
    }
}
