package com.axreng.backend.domain.service;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.service.processor.URLProcessor;
import com.axreng.backend.domain.service.manager.CrawlManager;
import com.axreng.backend.domain.service.manager.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);
    private static final int CRAWL_TIMEOUT_SECONDS = 300; // 5 minutes
    
    private final String baseUrl;
    URLProcessor urlProcessor;
    CrawlManager crawlManager;
    ThreadPoolManager threadPoolManager;

    public Crawler(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.threadPoolManager = new ThreadPoolManager();
        this.urlProcessor = new URLProcessor(this.baseUrl);
        this.crawlManager = new CrawlManager();
    }

    public void crawl(final Crawl crawl) {
        try {
            // Iniciar o crawl
            crawlManager.startCrawl(crawl);
            
            // Processar URL base
            urlProcessor.processUrl(baseUrl, crawl, threadPoolManager);

            // Verificar se não há tarefas
            if (crawlManager.hasNoActiveTasks(crawl)) {
                crawlManager.completeCrawl(crawl);
                return;
            }

            // Aguardar conclusão com timeout
            boolean completed = crawlManager.awaitCompletion(crawl, CRAWL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (!completed) {
                logger.warn("Crawl {} timed out after {} seconds", crawl.getId(), CRAWL_TIMEOUT_SECONDS);
            }

            // Finalizar o crawl
            crawlManager.completeCrawl(crawl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Crawl {} was interrupted", crawl.getId(), e);
            crawlManager.completeCrawl(crawl);
        } catch (Exception e) {
            logger.error("Error during crawl {}", crawl.getId(), e);
            crawlManager.completeCrawl(crawl);
        }
    }

    public void shutdown() {
        threadPoolManager.shutdown();
    }
}