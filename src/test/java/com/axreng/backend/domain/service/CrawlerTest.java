package com.axreng.backend.domain.service;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.service.manager.CrawlManager;
import com.axreng.backend.domain.service.manager.ThreadPoolManager;
import com.axreng.backend.domain.service.processor.URLProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CrawlerTest {

    @Mock
    private URLProcessor urlProcessor;

    @Mock
    private CrawlManager crawlManager;

    @Mock
    private ThreadPoolManager threadPoolManager;

    private Crawler crawler;
    private Crawl crawl;
    private static final String BASE_URL = "http://example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        crawler = new Crawler(BASE_URL);
        crawler.urlProcessor = urlProcessor;
        crawler.crawlManager = crawlManager;
        crawler.threadPoolManager = threadPoolManager;

        crawl = new Crawl("test-id", "keyword");
        crawl.setStatus("active");
        crawl.setCompletionLatch(new CountDownLatch(1));
        crawl.setActiveTasks(new AtomicInteger(0));
    }

    @Test
    void testCrawlSuccess() throws Exception {
        // Arrange
        when(crawlManager.hasNoActiveTasks(crawl)).thenReturn(false);
        when(crawlManager.awaitCompletion(any(), anyLong(), any())).thenReturn(true);

        // Act
        crawler.crawl(crawl);

        // Assert
        verify(crawlManager).startCrawl(crawl);
        verify(urlProcessor).processUrl(any(), any(), any());
        verify(crawlManager).completeCrawl(crawl);
    }

    @Test
    void testCrawlTimeout() throws Exception {
        // Arrange
        when(crawlManager.hasNoActiveTasks(crawl)).thenReturn(false);
        when(crawlManager.awaitCompletion(any(), anyLong(), any())).thenReturn(false);

        // Act
        crawler.crawl(crawl);

        // Assert
        verify(crawlManager).startCrawl(crawl);
        verify(crawlManager).completeCrawl(crawl);
    }

    @Test
    void testCrawlInterrupted() throws Exception {
        // Arrange
        when(crawlManager.hasNoActiveTasks(crawl)).thenReturn(false);
        when(crawlManager.awaitCompletion(any(), anyLong(), any()))
                .thenThrow(new InterruptedException());

        // Act
        crawler.crawl(crawl);

        // Assert
        verify(crawlManager).startCrawl(crawl);
        verify(crawlManager).completeCrawl(crawl);
    }

    @Test
    void testShutdown() {
        // Act
        crawler.shutdown();

        // Assert
        verify(threadPoolManager).shutdown();
    }
} 