package com.axreng.backend.infrastructure.config;

public class EnvironmentVariables {
    private static final String BASE_URL_ENV = "BASE_URL";
    private static final String PORT_ENV = "PORT";
    
    private static final String DEFAULT_BASE_URL = "http://hiring.axreng.com/";
    private static final int DEFAULT_PORT = 4567;
    
    public static String getBaseUrl() {
        String baseUrl = System.getenv(BASE_URL_ENV);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            return DEFAULT_BASE_URL;
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
            return DEFAULT_PORT;
        }
        
        try {
            return Integer.parseInt(portStr.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid PORT environment variable. Using default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
} 