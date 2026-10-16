package com.fastfood.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMetaDTO {
    private int id;
    private String title;
    private String description;
    private String category;
    private String sql;
}
