package com.codeflix.catalog.admin.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.codeflix.catalog.admin.infrastructure.configuration.WebServerConfig;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(WebServerConfig.class, args);
    }
}