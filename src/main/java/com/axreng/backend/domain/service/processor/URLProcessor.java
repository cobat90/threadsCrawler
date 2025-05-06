package com.axreng.backend.domain.service.processor;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.service.manager.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLProcessor {
    private static final Logger logger = LoggerFactory.getLogger(URLProcessor.class);
    // Pre-compile the pattern with optimized flags
    private static final Pattern URL_PATTERN = Pattern.compile("<a[^>]+href=[\"'](.*?)[\"']",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // Buffer size for reading from URL
    private static final int BUFFER_SIZE = 8192;

    private final String baseUrl;
    // Compile the keyword pattern once
    private Pattern keywordPattern;

    public URLProcessor(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void processUrl(String url, Crawl crawl, ThreadPoolManager threadPoolManager) {
        if (!crawl.getVisitedUrls().add(url)) {
            return; // Already visited
        }

        // Lazily initialize the keyword pattern
        if (keywordPattern == null) {
            keywordPattern = Pattern.compile(crawl.getKeyword().toLowerCase(), Pattern.CASE_INSENSITIVE);
        }

        crawl.getActiveTasks().incrementAndGet();
        threadPoolManager.submitTask(() -> {
            try {
                URL targetUrl = new URL(url);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(targetUrl.openConnection().getInputStream()),
                        BUFFER_SIZE);

                StringBuilder content = new StringBuilder();
                char[] buffer = new char[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = reader.read(buffer)) != -1) {
                    content.append(buffer, 0, bytesRead);
                }

                String pageContent = content.toString();

                // Check for keyword using more efficient method
                if (keywordPattern.matcher(pageContent).find()) {
                    crawl.addUrl(url);
                }

                // Use a more efficient approach for URL extraction
                Queue<String> foundUrls = extractUrls(pageContent);

                // Process URLs in batches
                while (!foundUrls.isEmpty()) {
                    String foundUrl = foundUrls.poll();
                    String absoluteUrl = resolveUrl(foundUrl);
                    if (shouldVisit(absoluteUrl)) {
                        processUrl(absoluteUrl, crawl, threadPoolManager);
                    }
                }

                reader.close();
            } catch (Exception e) {
                logger.error("Error processing URL: {}", url, e);
            } finally {
                checkCompletion(crawl);
            }
        });
    }

    private Queue<String> extractUrls(String content) {
        Queue<String> urls = new LinkedList<>();
        Matcher matcher = URL_PATTERN.matcher(content);

        // Extract all URLs at once instead of processing one by one
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }

        return urls;
    }

    private void checkCompletion(Crawl crawl) {
        int remainingTasks = crawl.getActiveTasks().decrementAndGet();
        if (remainingTasks == 0) {
            synchronized (crawl) {
                if (crawl.getActiveTasks().get() == 0) {
                    logger.info("All tasks completed for crawl {}. Signaling completion.", crawl.getId());
                    crawl.getCompletionLatch().countDown();
                }
            }
        }
    }

    public boolean shouldVisit(String url) {
        return url != null
                && url.startsWith(baseUrl)
                && !url.contains("../")
                && !url.contains("javascript:")
                && !url.contains("mailto:")
                && !url.contains("#");
    }

    public String resolveUrl(String foundUrl) {
        if (foundUrl == null || foundUrl.isEmpty()) {
            return null;
        }

        try {
            if (foundUrl.startsWith("http")) {
                return foundUrl;
            }
            if (foundUrl.startsWith("/")) {
                return baseUrl + foundUrl.substring(1);
            }
            return baseUrl + foundUrl;
        } catch (Exception e) {
            logger.error("Error resolving URL: {}", foundUrl, e);
            return null;
        }
    }
}