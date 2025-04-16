package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.config.ClickHouseConfig;

@SpringBootApplication(exclude = {JdbcRepositoriesAutoConfiguration.class,HttpClientAutoConfiguration.class,org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration.class})
@EnableConfigurationProperties(ClickHouseConfig.class)
public class ZeotapDataIngestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeotapDataIngestionApplication.class, args);
	}

}
