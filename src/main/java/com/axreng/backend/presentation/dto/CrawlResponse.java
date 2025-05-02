package com.axreng.backend.presentation.dto;

import java.util.List;

public class CrawlResponse {
    private String id;
    private String status;
    private List<String> urls;

    public CrawlResponse() {
    }

    public CrawlResponse(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
} 