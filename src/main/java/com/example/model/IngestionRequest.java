package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class IngestionRequest {
    private String source; // "clickhouse" or "flatfile"
    private String tableName;
    private List<String> columns;
    private String filePath;
    private String delimiter;
    private List<List<String>> data;
}
