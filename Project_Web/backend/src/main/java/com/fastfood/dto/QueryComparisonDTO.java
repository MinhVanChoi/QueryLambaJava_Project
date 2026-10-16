package com.fastfood.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryComparisonDTO {
    private int queryId;
    private String title;
    private QueryResultDTO sqlResult;
    private QueryResultDTO lambdaResult;
    private boolean matched;
    private String matchSummary;
}
