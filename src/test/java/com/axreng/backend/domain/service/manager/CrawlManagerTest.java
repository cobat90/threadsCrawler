package com.axreng.backend.domain.service.manager;

import com.axreng.backend.domain.model.Crawl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CrawlManagerTest {

    private CrawlManager crawlManager;
    private Crawl crawl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        crawlManager = new CrawlManager();
        crawl = new Crawl("test-id", "keyword");
        crawl.setStatus("active");
        crawl.setCompletionLatch(new CountDownLatch(1));
        crawl.setActiveTasks(new AtomicInteger(0));
    }

    @Test
    void testStartCrawl() {
        // Arrange
        crawl.getVisitedUrls().add("http://example.com");
        crawl.getActiveTasks().set(5);

        // Act
        crawlManager.startCrawl(crawl);

        // Assert
        assertTrue(crawl.getVisitedUrls().isEmpty());
        assertEquals(0, crawl.getActiveTasks().get());
    }

    @Test
    void testHasNoActiveTasks() {
        // Test with no active tasks
        assertTrue(crawlManager.hasNoActiveTasks(crawl));

        // Test with active tasks
        crawl.getActiveTasks().set(1);
        assertFalse(crawlManager.hasNoActiveTasks(crawl));
    }

    @Test
    void testAwaitCompletion() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        crawl.setCompletionLatch(latch);

        // Act & Assert
        assertFalse(crawlManager.awaitCompletion(crawl, 1, TimeUnit.MILLISECONDS));

        // Count down the latch
        latch.countDown();
        assertTrue(crawlManager.awaitCompletion(crawl, 1, TimeUnit.MILLISECONDS));
    }

    @Test
    void testCompleteCrawl() {
        // Test with active status
        crawl.setStatus("active");
        crawlManager.completeCrawl(crawl);
        assertEquals("done", crawl.getStatus());

        // Test with non-active status
        crawl.setStatus("done");
        crawlManager.completeCrawl(crawl);
        assertEquals("done", crawl.getStatus());
    }
} 