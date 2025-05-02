package com.axreng.backend.application.service;

import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;

public interface CrawlService {
    CrawlResponse startCrawl(CrawlRequest request);
    CrawlResponse getCrawlStatus(String id);
}
