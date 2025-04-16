
# 🛠️ ClickHouse & Flat File Ingestion Tool

A bi-directional data ingestion tool that allows transferring data between **ClickHouse** and **Flat Files** using a **Spring Boot** application. This tool supports both exporting data from ClickHouse to `.csv` format and ingesting data from `.csv` back into ClickHouse.

---

## 📌 Overview

This project provides an interface to:
- Export data from **ClickHouse** into a flat `.csv` file.
- Ingest `.csv` data back into **ClickHouse** seamlessly.

---

## 🎯 Objective

To build a flexible backend ingestion system that supports:
- Fast data retrieval and writing using ClickHouse.
- User interaction through a simple HTML UI.
- Integration with flat file systems (CSV format).

---

## 🧰 Tech Stack

| Technology       | Description                          |
|------------------|--------------------------------------|
| Java             | Backend language                     |
| Spring Boot      | Web and backend framework            |
| JDBC             | Connecting to ClickHouse             |
| ClickHouse       | Columnar OLAP DBMS                   |
| Docker           | Containerizing ClickHouse            |
| HTML / CSS       | Frontend interface                   |
| CSV              | Flat file format                     |
| GitHub           | Version control                      |

---

## 📂 Project Structure

```plaintext
clickhouse-flatfile-ingestion/
├── src/
│   ├── main/
│   │   ├── java/com/example/ingestion/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── model/
│   │   │   └── ZeotapDataIngestionApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/index.html
├── resources/
│   └── static/index.html
├── application.properties
├── README.md
└── pom.xml
```

---

## 🚀 Steps & Setup

### 1. 🐳 Run ClickHouse with Docker

```bash
docker run -d --name clickhouse-server -p 8123:8123 -p 9000:9000 yandex/clickhouse-server
```

Access ClickHouse shell:

```bash
docker exec -it clickhouse-server clickhouse-client
```

Create a database and table:

```sql
CREATE DATABASE uk;

CREATE TABLE uk.uk_price_paid
(
    price     UInt32,
    date      Date,
    postcode1 LowCardinality(String),
    postcode2 LowCardinality(String),
    type      Enum8('other' = 0, 'terraced' = 1, 'semi-detached' = 2, 'detached' = 3, 'flat' = 4),
    is_new    UInt8,
    duration  Enum8('unknown' = 0, 'freehold' = 1, 'leasehold' = 2),
    addr1     String,
    addr2     String,
    street    LowCardinality(String),
    locality  LowCardinality(String),
    town      LowCardinality(String),
    district  LowCardinality(String),
    county    LowCardinality(String)
) ENGINE = MergeTree()
ORDER BY (postcode1, postcode2);
```

Insert data or use the Spring Boot UI to ingest.

---

## 🧬 Spring Boot Dependencies (`pom.xml`)

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>ru.yandex.clickhouse</groupId>
        <artifactId>clickhouse-jdbc</artifactId>
        <version>0.3.2</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-csv</artifactId>
        <version>1.10.0</version>
    </dependency>
</dependencies>
```

---

## 🔧 `application.properties`

```properties
spring.datasource.url=jdbc:clickhouse://localhost:8123/uk
spring.datasource.username=default
spring.datasource.password=
clickhouse.driver-class-name=com.clickhouse.jdbc.ClickHouseDriver


```

---

## 📄 UI - `index.html`

```html
<!DOCTYPE html>
</head>
<body>
    <h1>Bidirectional Data Ingestion Tool</h1>

    <div class="section">
        <h3>Data Source Selection</h3>
        <label for="dataSource">Select Data Source:</label>
        <select id="dataSource" onchange="toggleSourceFields()">
            <option value="clickhouse">ClickHouse</option>
            <option value="flatfile">Flat File</option>
        </select>
    </div>

    <div id="clickhouseFields" class="section">
        <h3>ClickHouse Connection</h3>
        <label for="host">Host:</label>
        <input type="text" id="host" name="host"><br>
        <label for="port">Port:</label>
        <input type="number" id="port" name="port"><br>
        <label for="database">Database:</label>
        <input type="text" id="database" name="database"><br>
        <label for="jwtToken">JWT Token:</label>
        <input type="text" id="jwtToken" name="jwtToken"><br>
        <button onclick="connectClickHouse()">Connect</button>
        <div id="clickhouse-status" class="status"></div>
    </div>

    <div id="flatfileFields" class="section" style="display: none;">
        <h3>Flat File Configuration</h3>
        <label for="flatfile">Select Flat File (CSV):</label>
    <input type="file" id="flatfile" accept=".csv"><br><br>
        <label for="delimiter">Delimiter:</label>
        <input type="text" id="delimiter" name="delimiter" value=","><br>
        <label for="tableName">Table Name:</label>
		<input type="text" id="tableName2" name="tableName"><br>
        
        <button onclick="ingestToClickHouse()">Connect</button>
        <div id="flatfile-status" class="status"></div>
    </div>

    <div id="tableColumnSelection" class="section">
        <h3>Table/Column Selection</h3>
        <label for="tableName">Select Table:</label>
        <select id="tableName"></select>
        <button onclick="loadColumns()">Load Columns</button>
        <div id="columns"></div>
    </div>

    <div class="section">
        <h3>Ingestion Options</h3>
        <button onclick="ingestToClickHouse()">Ingestion from Flatfile to ClickHouse</button>
        <button onclick="ConnectHouseToFlatFile()">ClickHouse To FlatFile Ingestion</button>
       
        <div id="ingestion-status" class="status"></div>
    </div>

</body>
```

---

## 🔄 Ingestion Logic

### ➡️ ClickHouse → Flat File

```java
// In ExportService.java
String query = "SELECT * FROM uk.uk_price_paid";
ResultSet rs = statement.executeQuery(query);
CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
while (rs.next()) {
    printer.printRecord(...); // fill with table columns
}
```

### ⬅️ Flat File → ClickHouse

```java
// In ImportService.java
Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
for (CSVRecord record : records) {
    // extract data
    jdbcTemplate.update("INSERT INTO uk.uk_price_paid (...) VALUES (?, ?, ...)", ...);
}
```

---

## ✅ Final Result

- Start Spring Boot: `mvn spring-boot:run`
- Visit: `http://localhost:8080`
- Export or import your data with 1-click.
- Data will reflect in both ClickHouse and flat files (CSV format).

---

## ✍️ Author

**Balla Gayathri **  
💼 Java Backend Developer  
📧 Reach me on LinkedIn or GitHub!

---
