package com.fastfood.controller;

import com.fastfood.dto.ApiResponse;
import com.fastfood.dto.QueryComparisonDTO;
import com.fastfood.dto.QueryMetaDTO;
import com.fastfood.dto.QueryResultDTO;
import com.fastfood.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QueryMetaDTO>>> getAllQueries() {
        return ResponseEntity.ok(ApiResponse.ok(queryService.getAllQueryMetas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QueryMetaDTO>> getQueryMeta(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.ok(queryService.getQueryMeta(id)));
    }

    @GetMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<QueryResultDTO>> executeQueryGet(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "sql") String mode
    ) {
        QueryResultDTO result = queryService.executeQuery(id, mode);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<QueryResultDTO>> executeQueryPost(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "sql") String mode
    ) {
        QueryResultDTO result = queryService.executeQuery(id, mode);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}/compare")
    public ResponseEntity<ApiResponse<QueryComparisonDTO>> compareQuery(@PathVariable int id) {
        QueryComparisonDTO comparison = queryService.compareQuery(id);
        return ResponseEntity.ok(ApiResponse.ok(comparison));
    }
}
