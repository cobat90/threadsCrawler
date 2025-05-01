package com.axreng.backend;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.infrastructure.repository.InMemoryCrawlRepository;
import com.axreng.backend.controller.CrawlController;
import com.axreng.backend.config.Routes;
import com.axreng.backend.config.EnvironmentVariables;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Configure port
        port(EnvironmentVariables.getPort());

        // Initialize dependencies
        InMemoryCrawlRepository crawlRepository = new InMemoryCrawlRepository();
        Crawler crawler = new Crawler(EnvironmentVariables.getBaseUrl());
        CrawlService crawlService = new CrawlService(crawlRepository, crawler);
        CrawlController crawlController = new CrawlController(crawlService);
        Routes routes = new Routes(crawlController);

        // Configure routes
        routes.configure();

        // Configure CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Headers", "*");
            response.type("application/json");
        });
    }
}
