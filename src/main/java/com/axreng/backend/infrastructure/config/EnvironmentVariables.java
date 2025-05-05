package com.axreng.backend.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentVariables {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariables.class);
    private static final String BASE_URL_ENV = "BASE_URL";
    private static final String PORT_ENV = "PORT";
    
    private static final int DEFAULT_PORT = 4567;
    
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
    
    public static int getPort() {
        String portStr = System.getenv(PORT_ENV);
        if (portStr == null || portStr.trim().isEmpty()) {
            logger.error("Invalid PORT environment variable. Using default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
        
        try {
            return Integer.parseInt(portStr.trim());
        } catch (NumberFormatException e) {
            logger.error("Invalid PORT environment variable. Using default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
} 