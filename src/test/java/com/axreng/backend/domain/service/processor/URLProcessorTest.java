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
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

class URLProcessorTest {

    @Mock
    private ThreadPoolManager threadPoolManager;

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
        
        String urlStr = BASE_URL + "/test";
        crawl.getVisitedUrls().add(urlStr);

        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        assertEquals(0, crawl.getActiveTasks().get());
    }

    @Test
    void testProcessUrlSuccess() throws Exception {
        
        String urlStr = BASE_URL + "/test";
        String htmlContent = "<html><body><a href='/link1'>Link 1</a><a href='http://example.com/link2'>Link 2</a></body></html>";
        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());

        lenient().when(connection.getInputStream()).thenReturn(inputStream);

        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testProcessUrlWithKeyword() throws Exception {
        
        String urlStr = BASE_URL + "/test";
        String keyword = "test";
        String htmlContent = "<html><body>This is a test page</body></html>";
        InputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());

        lenient().when(connection.getInputStream()).thenReturn(inputStream);

        crawl.setKeyword(keyword);

        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testProcessUrlError() throws Exception {
        
        String urlStr = BASE_URL + "/test";

        urlProcessor.processUrl(urlStr, crawl, threadPoolManager);

        assertEquals(1, crawl.getActiveTasks().get());
        assertTrue(crawl.getVisitedUrls().contains(urlStr));
    }

    @Test
    void testShouldVisit() {
        assertTrue(urlProcessor.shouldVisit(BASE_URL + "/test"));
        assertTrue(urlProcessor.shouldVisit(BASE_URL + "/test/page"));

        assertFalse(urlProcessor.shouldVisit(null));
        assertFalse(urlProcessor.shouldVisit("http://other.com/test"));
        assertFalse(urlProcessor.shouldVisit(BASE_URL + "/../test"));
        assertFalse(urlProcessor.shouldVisit("javascript:void(0)"));
        assertFalse(urlProcessor.shouldVisit("mailto:test@example.com"));
        assertFalse(urlProcessor.shouldVisit(BASE_URL + "/test#section"));
    }

    @Test
    void testResolveUrl() {
        assertEquals("http://example.com/test", urlProcessor.resolveUrl("http://example.com/test"));
        assertEquals("https://example.com/test", urlProcessor.resolveUrl("https://example.com/test"));

        assertEquals(BASE_URL + "test", urlProcessor.resolveUrl("test"));

        assertNull(urlProcessor.resolveUrl(null));
        assertNull(urlProcessor.resolveUrl(""));
    }
}