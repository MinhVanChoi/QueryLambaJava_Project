package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.service.DatabaseInitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final DatabaseInitService databaseInitService;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseController(DatabaseInitService databaseInitService, JdbcTemplate jdbcTemplate) {
        this.databaseInitService = databaseInitService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/reinitialize")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reinitialize() {
        databaseInitService.reinitialize();
        Map<String, Object> status = getTableCounts();
        return ResponseEntity.ok(ApiResponse.ok("Database schema recreated and sample data reloaded successfully!", status));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = getTableCounts();
        return ResponseEntity.ok(ApiResponse.ok(status));
    }

    private Map<String, Object> getTableCounts() {
        Map<String, Object> map = new HashMap<>();
        String[] tables = {
                "chi_nhanh", "nhan_vien", "tai_khoan", "khach_hang", "danh_muc",
                "mon_an", "ton_kho", "khuyen_mai", "don_hang", "chi_tiet_don_hang", "thanh_toan"
        };
        for (String tbl : tables) {
            try {
                Integer cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tbl, Integer.class);
                map.put(tbl, cnt != null ? cnt : 0);
            } catch (Exception e) {
                map.put(tbl, -1);
            }
        }
        return map;
    }
}
