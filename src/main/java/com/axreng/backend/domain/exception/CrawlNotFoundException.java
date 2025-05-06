package com.axreng.backend.domain.exception;
 
public class CrawlNotFoundException extends RuntimeException {
    public CrawlNotFoundException(String id) {
        super("Crawl with id " + id + " not found");
    }
}