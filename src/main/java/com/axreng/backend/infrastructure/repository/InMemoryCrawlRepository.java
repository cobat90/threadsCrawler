package com.axreng.backend.infrastructure.repository;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCrawlRepository implements CrawlRepository {
    private final Map<String, Crawl> crawls = new ConcurrentHashMap<>();

    @Override
    public Crawl save(Crawl crawl) {
        crawls.put(crawl.getId(), crawl);
        return crawl;
    }

    @Override
    public Optional<Crawl> findById(String id) {
        return Optional.ofNullable(crawls.get(id));
    }

} 