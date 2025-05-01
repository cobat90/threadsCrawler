package com.axreng.backend.domain.repository;

import com.axreng.backend.domain.model.Crawl;
import java.util.List;
import java.util.Optional;

public interface CrawlRepository {
    Crawl save(Crawl crawl);
    Optional<Crawl> findById(String id);
    List<Crawl> findAll();
} 