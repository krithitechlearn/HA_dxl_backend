package com.ha.dxl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduler configuration for background jobs.
 * @see A-08, US-007
 */
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled via @EnableScheduling on HaDxlApplication
    // Telemetry purge job is in TelemetryServiceImpl
}
