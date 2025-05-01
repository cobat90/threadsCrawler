package com.axreng.backend.controller;

import com.axreng.backend.application.service.CrawlService;
import com.axreng.backend.model.CrawlRequest;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CrawlController {
    private final CrawlService crawlService;
    private final Gson gson;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
        this.gson = new Gson();
    }

    public String startCrawl(Request req, Response res) {
        res.type("application/json");
        try {
            CrawlRequest crawlRequest = gson.fromJson(req.body(), CrawlRequest.class);
            return crawlService.startCrawl(crawlRequest.getKeyword());
        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("error", "Internal server error"));
        }
    }

    public String getCrawlResults(Request req, Response res) {
        res.type("application/json");
        try {
            return crawlService.getCrawlResults(req.params(":id"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("error", "Internal server error"));
        }
    }
    
    public String getAllCrawls(Request req, Response res) {
        res.type("application/json");
        try {
            return crawlService.getAllCrawls();
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(Map.of("error", "Internal server error"));
        }
    }
} 