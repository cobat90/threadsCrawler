package com.axreng.backend.application.service;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.validation.KeywordValidator;
import com.google.gson.Gson;
import java.util.Map;

public class CrawlService {
    private final CrawlRepository crawlRepository;
    private final Crawler crawler;
    private final Gson gson;

    public CrawlService(CrawlRepository crawlRepository, Crawler crawler) {
        this.crawlRepository = crawlRepository;
        this.crawler = crawler;
        this.gson = new Gson();
    }

    public String startCrawl(String keyword) {
        KeywordValidator.validate(keyword);
        
        Crawl crawl = new Crawl(generateId(), keyword.trim());
        crawlRepository.save(crawl);
        
        // Start crawling in a new thread
        Thread crawlThread = new Thread(() -> {
            try {
                crawler.crawl(crawl);
            } finally {
                crawl.setStatus("done");
                crawlRepository.save(crawl);
            }
        });
        crawlThread.setName("CrawlThread-" + crawl.getId());
        crawlThread.start();
        
        return gson.toJson(Map.of("id", crawl.getId()));
    }

    public String getCrawlResults(String id) {
        return crawlRepository.findById(id)
            .map(crawl -> gson.toJson(crawl))
            .orElse(gson.toJson(Map.of("error", "Crawl not found")));
    }
    
    public String getAllCrawls() {
        return gson.toJson(crawlRepository.findAll());
    }

    private String generateId() {
        StringBuilder id = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(alphanumeric.length());
            id.append(alphanumeric.charAt(index));
        }
        
        return id.toString();
    }
} 