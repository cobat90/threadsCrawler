package com.axreng.backend.infrastructure.config;

import com.axreng.backend.domain.exception.CrawlNotFoundException;
import com.axreng.backend.presentation.controller.CrawlController;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Routes {
    private final CrawlController crawlController;
    private final Gson gson;

    public Routes(CrawlController crawlController) {
        this.crawlController = crawlController;
        this.gson = new Gson();
    }

    public void setup() {
        // Configure error handlers
        exception(IllegalArgumentException.class, (e, request, response) -> {
            response.status(400);
            response.type("application/json");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            response.body(gson.toJson(error));
        });

        exception(CrawlNotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.type("application/json");
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            response.body(gson.toJson(error));
        });

        exception(Exception.class, (e, request, response) -> {
            response.status(500);
            response.type("application/json");
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            response.body(gson.toJson(error));
        });

        // Configure routes
        post("/crawl", this::startCrawl, gson::toJson);
        get("/crawl/:id", this::getCrawlStatus, gson::toJson);
    }

    private CrawlResponse startCrawl(Request request, Response response) {
        return crawlController.startCrawl(request, response);
    }

    private CrawlResponse getCrawlStatus(Request request, Response response) {
        return crawlController.getCrawlStatus(request, response);
    }
} 