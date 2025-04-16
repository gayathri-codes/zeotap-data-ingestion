package com.example.service;

import com.example.config.ClickHouseConfig;
import com.example.model.IngestionRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClickHouseService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClickHouseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getTables(ClickHouseConfig config) {
        // Use the connection parameters from config if needed.
        String sql = "SELECT name FROM system.tables WHERE database = '" + config.getDatabase() + "'";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getColumns(String tableName) {
        String sql = "DESCRIBE TABLE " + tableName;
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"));
    }

    public void ingestData(String tableName, List<String> columns, List<List<String>> data) {
        for (List<String> row : data) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            sql.append(String.join(", ", columns)).append(") VALUES ('");
            sql.append(String.join("', '", row)).append("')");
            jdbcTemplate.execute(sql.toString());
        }
    }
    
    public void ingestData(IngestionRequest request) {
        ingestData(request.getTableName(), request.getColumns(), request.getData());
    }
    
    public byte[] exportSelectedColumnsToCsv(String tableName, List<String> columns) throws IOException {
        String joinedColumns = String.join(", ", columns);
        String query = "SELECT " + joinedColumns + " FROM " + tableName + " LIMIT 1000"; // or no limit

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        System.out.println("Rows fetched: " + rows.size());  // Add this
        System.out.println("Query: " + query);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter printer = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.withHeader(columns.toArray(new String[0])))) {
            for (Map<String, Object> row : rows) {
                List<Object> values = columns.stream().map(row::get).collect(Collectors.toList());
                printer.printRecord(values);
            }
        }

        return outputStream.toByteArray();
    }

}



//package com.example.service;
//
//import com.clickhouse.jdbc.ClickHouseDataSource;
//import com.example.config.ClickHouseConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//@Service
//@Slf4j
//public class ClickHouseService {
//
//    @Autowired
//    private ClickHouseConfig clickHouseConfig;
//
//    private Connection getConnection() throws SQLException {
////        ClickHouseDataSource dataSource = new ClickHouseDataSource(
//            String url=String.format("jdbc:clickhouse://%s:%d/%s",
//                clickHouseConfig.getHost(),
//                clickHouseConfig.getPort(),
//                clickHouseConfig.getDatabase());
//            Properties props = new Properties();
//            // If you need to supply a username or password, you can set them here:
//            // props.setProperty("user", "yourUsername");
//            // props.setProperty("password", "yourPassword");
//            
//            // Set JWT if provided (adjust the property name as expected by your driver)
//            if (clickHouseConfig.getJwtToken() != null && !clickHouseConfig.getJwtToken().isEmpty()) {
//                props.setProperty("jwt", clickHouseConfig.getJwtToken());
//            }
//            
//            // Create the data source using the URL and properties
//            ClickHouseDataSource dataSource = new ClickHouseDataSource(url, props);
//            
//
//        
//        return dataSource.getConnection();
//    }
//
//    public List<String> getTables() {
//        List<String> tables = new ArrayList<>();
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {
//
//            while (resultSet.next()) {
//                tables.add(resultSet.getString(1));
//            }
//        } catch (SQLException e) {
//            log.error("Error fetching tables:", e);
//        }
//        return tables;
//    }
//
//    public List<String> getColumns(String tableName) {
//        List<String> columns = new ArrayList<>();
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery("DESCRIBE TABLE " + tableName)) {
//
//            while (resultSet.next()) {
//                columns.add(resultSet.getString(1));
//            }
//        } catch (SQLException e) {
//            log.error("Error fetching columns for table {}:", tableName, e);
//        }
//        return columns;
//    }
//
//    public void ingestData(String tableName, List<String> columns, List<List<String>> data) {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//
//            for (List<String> row : data) {
//                StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
//                sql.append(String.join(", ", columns)).append(") VALUES ('");
//                sql.append(String.join("', '", row)).append("')");
//                statement.execute(sql.toString());
//            }
//        } catch (SQLException e) {
//            log.error("Error ingesting data into table {}:", tableName, e);
//        }
//    }
//}
