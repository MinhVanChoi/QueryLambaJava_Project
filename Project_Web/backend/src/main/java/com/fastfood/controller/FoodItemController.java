package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.entity.MonAn;
import com.fastfood.enums.TrangThaiMon;
import com.fastfood.service.MonAnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food-items")
public class FoodItemController {

    private final MonAnService service;

    public FoodItemController(MonAnService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MonAn>>> getAll(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) TrangThaiMon status,
            @RequestParam(required = false) String sortBy
    ) {
        List<MonAn> list;
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            list = service.sortPriceAsc();
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            list = service.sortPriceDescThenName();
        } else if (categoryId != null && !categoryId.isBlank()) {
            list = service.getByCategory(categoryId);
        } else if (status != null) {
            list = service.getByStatus(status);
        } else {
            list = service.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonAn>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/most-expensive")
    public ResponseEntity<ApiResponse<MonAn>> getMostExpensive() {
        return service.findMostExpensive()
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MonAn>> create(@Valid @RequestBody MonAn food) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Food item created successfully", service.create(food)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MonAn>> update(@PathVariable String id, @Valid @RequestBody MonAn food) {
        return ResponseEntity.ok(ApiResponse.ok("Food item updated successfully", service.update(id, food)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> patchField(
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String field = body.get("field");
        String value = body.get("value");
        if (field == null || value == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Both 'field' and 'value' are required"));
        }
        boolean ok = service.updateField(id, field, value);
        return ResponseEntity.ok(ApiResponse.ok("Field updated", ok));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Food item deleted successfully", null));
    }
}
