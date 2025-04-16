package com.example.service;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlatFileService {

    public List<List<String>> readCsvFile(String filePath, String delimiter) {
        List<List<String>> records = null;
        try (FileReader fileReader = new FileReader(filePath)) {
            CSVParserBuilder parserBuilder = new CSVParserBuilder().withSeparator(delimiter.charAt(0));
            CSVReaderBuilder readerBuilder = new CSVReaderBuilder(fileReader).withCSVParser(parserBuilder.build());
            CSVReader csvReader = readerBuilder.build();
            List<String[]> lines = csvReader.readAll();

            // Convert String[] to List<String> for each line
            records = lines.stream()
                    .map(Arrays::asList)
                    .collect(Collectors.toList());
        } catch (IOException | CsvException e) {
            log.error("Error reading CSV file:", e);
        }
        return records;
    }
    public void writeCsvFile(String filePath, List<List<String>> data, char delimiter) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(delimiter))) {
            for (List<String> row : data) {
                printer.printRecord(row);
            }
        }
    }
}
