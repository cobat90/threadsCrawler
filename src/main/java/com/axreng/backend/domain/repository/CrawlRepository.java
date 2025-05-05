package com.axreng.backend.domain.repository;

import com.axreng.backend.domain.model.Crawl;

import java.util.Optional;

public interface CrawlRepository {
    void save(Crawl crawl);
    Optional<Crawl> findById(String id);
}