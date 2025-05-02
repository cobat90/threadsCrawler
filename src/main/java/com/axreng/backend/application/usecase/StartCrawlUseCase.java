package com.axreng.backend.application.usecase;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.domain.validation.KeywordValidator;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartCrawlUseCase {
    private final CrawlRepository crawlRepository;
    private final Crawler crawler;
    private final ExecutorService executorService;

    public StartCrawlUseCase(CrawlRepository crawlRepository, Crawler crawler) {
        this.crawlRepository = crawlRepository;
        this.crawler = crawler;
        this.executorService = Executors.newCachedThreadPool();
    }

    public CrawlResponse execute(CrawlRequest request) {
        KeywordValidator.validate(request.getKeyword());
        
        String id = UUID.randomUUID().toString();
        Crawl crawl = new Crawl(id, request.getKeyword());
        crawlRepository.save(crawl);

        executorService.submit(() -> {
            try {
                crawler.crawl(crawl);
                crawl.setStatus("done");
            } catch (Exception e) {
                crawl.setStatus("error");
            }
            crawlRepository.save(crawl);
        });

        return new CrawlResponse(id, crawl.getStatus(), crawl.getUrls());
    }
} 