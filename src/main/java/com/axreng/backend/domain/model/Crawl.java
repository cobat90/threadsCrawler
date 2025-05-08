package com.axreng.backend.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawl {
    private final String id;
    private String keyword;
    private String status;
    private final List<String> urls;
    private final Set<String> visitedUrls;
    private CountDownLatch completionLatch;
    private AtomicInteger activeTasks;

    public Crawl(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        this.status = "active";
        this.urls = new ArrayList<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.completionLatch = new CountDownLatch(1);
        this.activeTasks = new AtomicInteger(0);
    }

    public String getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        System.out.println("Crawl " + id + " status changed to: " + status);
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

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    public CountDownLatch getCompletionLatch() {
        return completionLatch;
    }

    public void setCompletionLatch(CountDownLatch completionLatch) {
        this.completionLatch = completionLatch;
    }

    public AtomicInteger getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(AtomicInteger activeTasks) {
        this.activeTasks = activeTasks;
    }
}