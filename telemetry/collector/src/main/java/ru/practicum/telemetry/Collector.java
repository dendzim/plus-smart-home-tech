package ru.practicum.telemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}