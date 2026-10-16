package com.fastfood.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResultDTO {
    private int queryId;
    private String title;
    private String description;
    private String sql;
    private String executionMode; // "SQL" or "STREAM_API"
    @Builder.Default
    private List<String> headers = new ArrayList<>();
    @Builder.Default
    private List<List<Object>> rows = new ArrayList<>();
    private int totalRows;
    private long executionTimeMs;
}
