package com.axreng.backend.domain.service.processor;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.service.manager.ThreadPoolManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

class URLProcessorTest {

    @Mock
    private ThreadPoolManager threadPoolManager;

    @Mock
    private URL url;

    @Mock
    private URLConnection connection;

    @InjectMocks
    private URLProcessor urlProcessor;

    private Crawl crawl;
    private static final String BASE_URL = "http://example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        urlProcessor = new URLProcessor(BASE_URL);
        crawl = new Crawl("test-id", "keyword");
        crawl.setStatus("active");
        crawl.setCompletionLatch(new CountDownLatch(1));
        crawl.setActiveTasks(new AtomicInteger(0));
    }

    @AfterEach
    void tearDown() {
        threadPoolManager.shutdown();
    }

    @Test
    void testProcessUrlAlreadyVisited() {
        // Arrange
        String urlStr = BASE_URL + "/test";
        crawl.getVisitedUrls().add(urlStr);

        // Act
        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        // Assert
        assertEquals(0, crawl.getActiveTasks().get());
    }

    @Test
    void testProcessUrlSuccess() throws Exception {
        // Arrange
        String urlStr = BASE_URL + "/test";
        String htmlContent = "<html><body><a href='/link1'>Link 1</a><a href='http://example.com/link2'>Link 2</a></body></html>";
        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());

        lenient().when(url.openConnection()).thenReturn(connection);
        lenient().when(connection.getInputStream()).thenReturn(inputStream);

        // Act
        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        // Assert
        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testProcessUrlWithKeyword() throws Exception {
        // Arrange
        String urlStr = BASE_URL + "/test";
        String keyword = "test";
        String htmlContent = "<html><body>This is a test page</body></html>";
        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());

        lenient().when(url.openConnection()).thenReturn(connection);
        lenient().when(connection.getInputStream()).thenReturn(inputStream);

        crawl.setKeyword(keyword);

        // Act
        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        // Assert
        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testProcessUrlError() throws Exception {
        // Arrange
        String urlStr = BASE_URL + "/test";
        lenient().when(url.openConnection()).thenThrow(new RuntimeException("Connection error"));

        // Act
        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        // Assert
        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testShouldVisit() {
        // Test valid URLs
        assertTrue(urlProcessor.shouldVisit(BASE_URL + "/test"));
        assertTrue(urlProcessor.shouldVisit(BASE_URL + "/test/page"));

        // Test invalid URLs
        assertFalse(urlProcessor.shouldVisit(null));
        assertFalse(urlProcessor.shouldVisit("http://other.com/test"));
        assertFalse(urlProcessor.shouldVisit(BASE_URL + "/../test"));
        assertFalse(urlProcessor.shouldVisit("javascript:void(0)"));
        assertFalse(urlProcessor.shouldVisit("mailto:test@example.com"));
        assertFalse(urlProcessor.shouldVisit(BASE_URL + "/test#section"));
    }

    @Test
    void testResolveUrl() {
        // Test absolute URLs
        assertEquals("http://example.com/test", urlProcessor.resolveUrl("http://example.com/test"));
        assertEquals("https://example.com/test", urlProcessor.resolveUrl("https://example.com/test"));

        // Test relative URLs
        assertEquals(BASE_URL + "test", urlProcessor.resolveUrl("test"));

        // Test invalid URLs
        assertNull(urlProcessor.resolveUrl(null));
        assertNull(urlProcessor.resolveUrl(""));
    }
}