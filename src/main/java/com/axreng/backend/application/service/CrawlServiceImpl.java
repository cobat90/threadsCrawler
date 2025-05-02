package com.axreng.backend.application.service;

import com.axreng.backend.application.usecase.GetCrawlStatusUseCase;
import com.axreng.backend.application.usecase.StartCrawlUseCase;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;

public class CrawlServiceImpl implements CrawlService {
    private final StartCrawlUseCase startCrawlUseCase;
    private final GetCrawlStatusUseCase getCrawlStatusUseCase;

    public CrawlServiceImpl(StartCrawlUseCase startCrawlUseCase, GetCrawlStatusUseCase getCrawlStatusUseCase) {
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
} 