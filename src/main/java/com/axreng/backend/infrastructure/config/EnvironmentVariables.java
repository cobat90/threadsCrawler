package com.axreng.backend.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentVariables {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariables.class);
    private static final String BASE_URL_ENV = "BASE_URL";

    public static String getBaseUrl() {
        String baseUrl = System.getenv(BASE_URL_ENV);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            logger.error("Invalid BASE_URL_ENV environment variable.");
        }
        
        // Ensure the base URL ends with a slash
        baseUrl = baseUrl.trim();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        return baseUrl;
    }
} 