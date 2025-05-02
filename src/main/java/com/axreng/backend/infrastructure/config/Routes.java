package com.axreng.backend.infrastructure.config;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

public class Routes {
    private final CrawlService crawlService;
    private final Gson gson;

    public Routes(CrawlService crawlService) {
        this.crawlService = crawlService;
        this.gson = new Gson();
    }

    public void setup() {
        post("/crawl", this::startCrawl, gson::toJson);
        get("/crawl/:id", this::getCrawlStatus, gson::toJson);
    }

    private CrawlResponse startCrawl(Request request, Response response) {
        CrawlRequest crawlRequest = gson.fromJson(request.body(), CrawlRequest.class);
        return crawlService.startCrawl(crawlRequest);
    }

    private CrawlResponse getCrawlStatus(Request request, Response response) {
        String id = request.params(":id");
        return crawlService.getCrawlStatus(id);
    }
} 