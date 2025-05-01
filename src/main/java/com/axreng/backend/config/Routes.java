package com.axreng.backend.config;

import com.axreng.backend.controller.CrawlController;
import static spark.Spark.*;

public class Routes {
    private final CrawlController crawlController;

    public Routes(CrawlController crawlController) {
        this.crawlController = crawlController;
    }

    public void configure() {
        // Enable CORS
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

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        // Configure routes
        post("/crawl", crawlController::startCrawl);
        get("/crawl/:id", crawlController::getCrawlResults);
        get("/crawl", crawlController::getAllCrawls);
    }
} 