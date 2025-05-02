package com.axreng.backend.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Crawl {
    private final String id;
    private final AtomicReference<String> status;
    private final List<String> urls;
    private final String keyword;
    private final CountDownLatch completionLatch;
    private final AtomicInteger activeTasks;
    private final Set<String> visitedUrls;

    public Crawl(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        this.status = new AtomicReference<>("active");
        this.urls = new ArrayList<>();
        this.completionLatch = new CountDownLatch(1);
        this.activeTasks = new AtomicInteger(0);
        this.visitedUrls = ConcurrentHashMap.newKeySet();
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public List<String> getUrls() {
        return new ArrayList<>(urls);
    }

    public void addUrl(String url) {
        synchronized (urls) {
            if (!urls.contains(url)) {
                urls.add(url);
            }
        }
    }

    public String getKeyword() {
        return keyword;
    }

    public CountDownLatch getCompletionLatch() {
        return completionLatch;
    }

    public AtomicInteger getActiveTasks() {
        return activeTasks;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
}