package com.axreng.backend.infrastructure.config;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.presentation.controller.CrawlController;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Routes {
    private final CrawlController crawlController;
    private final Gson gson;

    public Routes(CrawlController crawlController) {
        this.crawlController = crawlController;
        this.gson = new Gson();
    }

    public void setup() {
        post("/crawl", this::startCrawl, gson::toJson);
        get("/crawl/:id", this::getCrawlStatus, gson::toJson);
    }

    private Object startCrawl(Request request, Response response) {
        return crawlController.startCrawl(request, response);
    }

    private Object getCrawlStatus(Request request, Response response) {
        return crawlController.getCrawlStatus(request, response);
    }
} 