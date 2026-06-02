package com.bfsi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication

// ✅ ENSURE JPA PICKS UP REPOSITORY
@EnableJpaRepositories(basePackages = "com.bfsi.repository")

// ✅ ENSURE ENTITY IS SCANNED
@EntityScan(basePackages = "com.bfsi.entity")

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}