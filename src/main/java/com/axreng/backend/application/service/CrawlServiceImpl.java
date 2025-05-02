package com.axreng.backend.application.service;

import com.axreng.backend.application.usecase.GetCrawlStatusUseCase;
import com.axreng.backend.application.usecase.StartCrawlUseCase;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.google.gson.Gson;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

public class CrawlServiceImpl implements CrawlService {
    private final CrawlRepository crawlRepository;
    private final Crawler crawler;
    private final Gson gson;
    private final ExecutorService executorService;
    private final StartCrawlUseCase startCrawlUseCase;
    private final GetCrawlStatusUseCase getCrawlStatusUseCase;

    public CrawlServiceImpl(CrawlRepository crawlRepository, Crawler crawler, StartCrawlUseCase startCrawlUseCase, GetCrawlStatusUseCase getCrawlStatusUseCase) {
        this.crawlRepository = crawlRepository;
        this.crawler = crawler;
        this.gson = new Gson();
        this.executorService = Executors.newCachedThreadPool();
        this.startCrawlUseCase = startCrawlUseCase;
        this.getCrawlStatusUseCase = getCrawlStatusUseCase;
    }

    @Override
    public CrawlResponse startCrawl(CrawlRequest request) {
        return startCrawlUseCase.execute(request);
    }

    @Override
    public CrawlResponse getCrawlStatus(String id) {
        return getCrawlStatusUseCase.execute(id);
    }

    @Override
    public String getCrawlResults(String id) {
        return crawlRepository.findById(id)
            .map(gson::toJson)
            .orElse(gson.toJson(Map.of("error", "Crawl not found")));
    }

    @Override
    public String getAllCrawls() {
        return gson.toJson(crawlRepository.findAll());
    }
} 