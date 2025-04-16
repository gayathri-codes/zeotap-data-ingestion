package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;



@Configuration
@ConfigurationProperties(prefix = "clickhouse")
@Setter
@Getter
public class ClickHouseConfig {

    private String host;
    private int port;
    private String database;
    private String jwtToken;
    
}
