package com.example.ams.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ams")
public record AppProperties(int maxShiftHours, String uploadDir) {}
