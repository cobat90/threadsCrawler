package com.axreng.backend.application.usecase;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.domain.util.IdGenerator;
import com.axreng.backend.domain.validation.KeywordValidator;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StartCrawlUseCase {
    private static final int MAX_CONCURRENT_CRAWLS = 5;
    private static final int CRAWL_TIMEOUT_MINUTES = 5;

    private final CrawlRepository crawlRepository;
    private final Crawler crawler;
    private final ExecutorService executorService;

    public StartCrawlUseCase(CrawlRepository crawlRepository, Crawler crawler) {
        this.crawlRepository = crawlRepository;
        this.crawler = crawler;
        this.executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_CRAWLS);
    }

    public CrawlResponse execute(CrawlRequest request) {
        KeywordValidator.validate(request.getKeyword());

        String id = IdGenerator.generate();
        Crawl crawl = new Crawl(id, request.getKeyword());
        crawl.setStatus("active");
        crawlRepository.save(crawl);

        executorService.submit(() -> {
            try {
                crawler.crawl(crawl);
            } finally {
                crawlRepository.save(crawl);
            }
        });

        return new CrawlResponse(id);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(CRAWL_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}