package com.axreng.backend.presentation.controller;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

public class CrawlController {
    private final CrawlService crawlService;
    private final Gson gson;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
        this.gson = new Gson();
    }

    public CrawlResponse startCrawl(Request request, Response response) {
        CrawlRequest crawlRequest = gson.fromJson(request.body(), CrawlRequest.class);
        return crawlService.startCrawl(crawlRequest);
    }

    public CrawlResponse getCrawlStatus(Request request, Response response) {
        String id = request.params(":id");
        return crawlService.getCrawlStatus(id);
    }
} 