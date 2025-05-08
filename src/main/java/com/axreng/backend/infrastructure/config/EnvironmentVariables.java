package com.axreng.backend.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class EnvironmentVariables {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariables.class);
    private static final String BASE_URL_ENV = "BASE_URL";

    public static String getBaseUrl() {
        String baseUrl = System.getenv(BASE_URL_ENV);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            logger.error("Invalid BASE_URL_ENV environment variable.");
        }
        
        baseUrl = Objects.requireNonNull(baseUrl).trim();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        return baseUrl;
    }
} 