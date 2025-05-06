package com.axreng.backend.application.usecase;

import com.axreng.backend.domain.exception.CrawlNotFoundException;
import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.presentation.dto.CrawlResponse;

public class GetCrawlStatusUseCase {
    private final CrawlRepository crawlRepository;

    public GetCrawlStatusUseCase(CrawlRepository crawlRepository) {
        this.crawlRepository = crawlRepository;
    }

    public CrawlResponse execute(String id) {
        return crawlRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CrawlNotFoundException(id));
    }

    private CrawlResponse toResponse(Crawl crawl) {
        return new CrawlResponse(
            crawl.getId(),
            crawl.getStatus(),
            crawl.getUrls()
        );
    }
} 