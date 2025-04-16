package com.example.controller;

import com.example.model.IngestionRequest;
import com.example.config.ClickHouseConfig;
import com.example.dto.TableRequest;
import com.example.service.ClickHouseService;
import com.example.service.FlatFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class IngestionController {

    private final ClickHouseService clickHouseService;
    private final FlatFileService fileService;

    @Autowired
    public IngestionController(ClickHouseService clickHouseService, FlatFileService fileService) {
        this.clickHouseService = clickHouseService;
        this.fileService = fileService;
    }

    // Connect to ClickHouse and return list of tables.
    @PostMapping("/connect-clickhouse")
    public ResponseEntity<List<String>> connectClickHouse(@RequestBody ClickHouseConfig config) {
        try {
            List<String> tables = clickHouseService.getTables(config);
            return new ResponseEntity<>(tables, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error connecting to ClickHouse:", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/get-columns")
    public ResponseEntity<List<String>> getColumns(@RequestBody TableRequest request) {
        try {
            String tableName = request.getTableName();
            List<String> columns = clickHouseService.getColumns(tableName);
            return new ResponseEntity<>(columns, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting columns for table {}:", request.getTableName(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @PostMapping("/get-columns")
//    public ResponseEntity<List<String>> getColumns(@RequestBody String tableName) {
//        try {
//            List<String> columns = clickHouseService.getColumns(tableName);
//            return new ResponseEntity<>(columns, HttpStatus.OK);
//        } catch (Exception e) {
//            log.error("Error getting columns for table {}:", tableName, e);
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//    @GetMapping("/api/columns")
//    public ResponseEntity<Map<String, List<String>>> getColumns(@RequestParam String table) {
//        String query = "DESCRIBE TABLE " + table;
//        List<String> columns = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("name"));
//        return ResponseEntity.ok(Map.of("columns", columns));
//    }

    @PostMapping("/export-to-csv")
    public ResponseEntity<byte[]> exportToCsv(@RequestBody Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        List<String> selectedColumns = (List<String>) request.get("selectedColumns");

        try {
            byte[] csvData = clickHouseService.exportSelectedColumnsToCsv(tableName, selectedColumns);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + tableName + ".csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/ingest-to-clickhouse")
    public String ingestToClickHouse(@RequestBody IngestionRequest request) {
        if ("flatfile".equals(request.getSource())) {
            // Process flatfile ingestion
            processFlatFileIngestion(request);
        } else if ("clickhouse".equals(request.getSource())) {
            // Process direct data ingestion (if needed)
            processClickHouseIngestion(request);
        }
        return "Ingestion completed successfully.";
    }

    // FlatFile ingestion logic
    private void processFlatFileIngestion(IngestionRequest request) {
        // You can get the file path, delimiter, and data from the request
        String filePath = request.getFilePath();
        String delimiter = request.getDelimiter();
        List<List<String>> data = request.getData();

        // Logic to process flatfile (e.g., save data or process it)
        // For example, parsing CSV, saving to database, etc.
        System.out.println("Processing FlatFile Ingestion...");
        // Handle flatfile data ingestion to ClickHouse
    }

    // ClickHouse ingestion logic (if needed)
    private void processClickHouseIngestion(IngestionRequest request) {
        // Process data directly from request (tableName, columns, and data)
        String tableName = request.getTableName();
        List<String> columns = request.getColumns();
        List<List<String>> data = request.getData();

        // Logic to process data and ingest into ClickHouse
        System.out.println("Processing ClickHouse Ingestion...");
        // Perform insertion to ClickHouse or other relevant operations
    }


    @PostMapping("/ingest-from-clickhouse")
    public String ingestFromClickHouse(@RequestBody IngestionRequest request) throws IOException {
        List<List<String>> data = fetchDataFromClickHouse(request.getTableName(), request.getColumns());
        fileService.writeCsvFile("hardcoded_filepath", data, ',');
        return "Ingestion completed successfully.";
    }

    private List<List<String>> fetchDataFromClickHouse(String tableName, List<String> columns) {
        // Dummy data for demonstration.
        return List.of(
            List.of("Value1", "Value2"),
            List.of("Value3", "Value4")
        );
    }
}
