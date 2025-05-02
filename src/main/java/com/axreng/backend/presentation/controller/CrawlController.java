package com.axreng.backend.presentation.controller;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class CrawlController {
    private final CrawlService crawlService;
    private final Gson gson;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
        this.gson = new Gson();
    }

    public Object startCrawl(Request request, Response response) {
        try {
            CrawlRequest crawlRequest = gson.fromJson(request.body(), CrawlRequest.class);
            return crawlService.startCrawl(crawlRequest);
        } catch (IllegalArgumentException e) {
            response.status(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        } catch (Exception e) {
            response.status(500);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return error;
        }
    }

    public Object getCrawlStatus(Request request, Response response) {
        try {
            String id = request.params(":id");
            return crawlService.getCrawlStatus(id);
        } catch (Exception e) {
            response.status(500);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return error;
        }
    }
} 