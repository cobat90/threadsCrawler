package com.axreng.backend;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.application.service.CrawlServiceImpl;
import com.axreng.backend.application.usecase.GetCrawlStatusUseCase;
import com.axreng.backend.application.usecase.StartCrawlUseCase;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.infrastructure.config.EnvironmentVariables;
import com.axreng.backend.infrastructure.config.Routes;
import com.axreng.backend.infrastructure.repository.InMemoryCrawlRepository;
import com.axreng.backend.presentation.controller.CrawlController;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Configure port
        port(EnvironmentVariables.getPort());

        // Initialize domain layer
        CrawlRepository crawlRepository = new InMemoryCrawlRepository();
        Crawler crawler = new Crawler(EnvironmentVariables.getBaseUrl());

        // Initialize application layer
        StartCrawlUseCase startCrawlUseCase = new StartCrawlUseCase(crawlRepository, crawler);
        GetCrawlStatusUseCase getCrawlStatusUseCase = new GetCrawlStatusUseCase(crawlRepository);
        CrawlService crawlService = new CrawlServiceImpl(startCrawlUseCase, getCrawlStatusUseCase);

        // Initialize infrastructure layer
        CrawlController crawlController = new CrawlController(crawlService);
        Routes routes = new Routes(crawlController);
        routes.setup();

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
