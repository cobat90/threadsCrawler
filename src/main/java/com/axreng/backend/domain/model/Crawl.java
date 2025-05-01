package com.axreng.backend.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Crawl {
    private final String id;
    private String status;
    private final List<String> urls;
    private final String keyword;

    public Crawl(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        this.status = "active";
        this.urls = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getUrls() {
        return new ArrayList<>(urls);
    }

    public void addUrl(String url) {
        if (!urls.contains(url)) {
            urls.add(url);
        }
    }

    public String getKeyword() {
        return keyword;
    }
} 