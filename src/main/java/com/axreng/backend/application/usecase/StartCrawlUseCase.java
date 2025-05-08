package com.axreng.backend.application.usecase;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.repository.CrawlRepository;
import com.axreng.backend.domain.service.Crawler;
import com.axreng.backend.domain.util.IdGenerator;
import com.axreng.backend.domain.validation.KeywordValidator;
import com.axreng.backend.presentation.dto.CrawlRequest;
import com.axreng.backend.presentation.dto.CrawlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StartCrawlUseCase {
    private static final int MAX_CONCURRENT_CRAWLS = 5;
    private static final int CRAWL_TIMEOUT_MINUTES = 5;

    private final CrawlRepository crawlRepository;
    private final Crawler crawler;
    private final ExecutorService executorService;
    private static final Logger logger = LoggerFactory.getLogger(StartCrawlUseCase.class);

    public StartCrawlUseCase(CrawlRepository crawlRepository, Crawler crawler) {
        this.crawlRepository = crawlRepository;
        this.crawler = crawler;
        this.executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_CRAWLS);
    }

    public CrawlResponse execute(CrawlRequest request) {
        KeywordValidator.validate(request.getKeyword());

        String id = IdGenerator.generate();
        Crawl crawl = new Crawl(id, request.getKeyword());
        crawl.setStatus("active");
        crawlRepository.save(crawl);

        // Criando uma thread dedicada para monitorar a conclusão deste crawl específico
        Thread monitorThread = new Thread(() -> {
            try {
                boolean completed = crawl.getCompletionLatch().await(CRAWL_TIMEOUT_MINUTES, TimeUnit.MINUTES);

                synchronized (crawl) {
                    if (completed) {
                        crawl.setStatus("done");
                        logger.info("Crawl {} completed successfully. Status changed to done.", crawl.getId());
                    } else {
                        crawl.setStatus("timeout");
                        logger.error("Crawl {} timed out after " + CRAWL_TIMEOUT_MINUTES + " minutes", crawl.getId());
                    }
                    crawlRepository.save(crawl);
                }
            } catch (Exception e) {
                logger.error("Error monitoring crawl process: {}", e.getMessage());
                synchronized (crawl) {
                    crawl.setStatus("error");
                    crawlRepository.save(crawl);
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        // Inicia o processo de crawling em outra thread do pool
        executorService.submit(() -> {
            try {
                crawler.crawl(crawl);
            } catch (Exception e) {
                logger.error("Error in crawl process: {}", e.getMessage());
                synchronized (crawl) {
                    crawl.setStatus("error");
                    crawlRepository.save(crawl);
                }
            }
        });

        return new CrawlResponse(id);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(CRAWL_TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}