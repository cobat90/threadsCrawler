package com.axreng.backend.application.usecase;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.presentation.dto.CrawlResponse;

import java.util.Optional;

public class GetCrawlStatusUseCase {
    private final CrawlRepository crawlRepository;

    public GetCrawlStatusUseCase(CrawlRepository crawlRepository) {
        this.crawlRepository = crawlRepository;
    }

    public CrawlResponse execute(String id) {
        Optional<Crawl> optCrawl = crawlRepository.findById(id);
        if (optCrawl.isEmpty()) {
            return null;
        }
        Crawl crawl = optCrawl.get();
        return new CrawlResponse(crawl.getId(), crawl.getStatus(), crawl.getUrls());
    }
} 