package com.ha.dxl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the HA DXL (Home Automation Device Experience Layer) backend application.
 * Enables JPA auditing, async processing, and scheduling.
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware")
@EnableAsync
@EnableScheduling
public class HaDxlApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaDxlApplication.class, args);
    }
}
