package com.axreng.backend.domain.service.manager;

import com.axreng.backend.domain.model.Crawl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CrawlManager {
    private static final Logger logger = LoggerFactory.getLogger(CrawlManager.class);

    public void startCrawl(Crawl crawl) {
        logger.info("Starting crawl {}", crawl.getId());
        crawl.getVisitedUrls().clear();
        crawl.getActiveTasks().set(0);
    }

    public boolean hasNoActiveTasks(Crawl crawl) {
        return crawl.getActiveTasks().get() == 0;
    }

    public boolean awaitCompletion(Crawl crawl, long timeout, TimeUnit unit) throws InterruptedException {
        return crawl.getCompletionLatch().await(timeout, unit);
    }

    public void completeCrawl(Crawl crawl) {
        synchronized (crawl) {
            if (crawl.getStatus().equals("active")) {
                logger.info("Completing crawl {}", crawl.getId());
                crawl.setStatus("done");
            }
        }
    }
} 