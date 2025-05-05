package com.axreng.backend.domain.service.processor;

import com.axreng.backend.domain.model.Crawl;
import com.axreng.backend.domain.service.manager.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLProcessor {
    private static final Logger logger = LoggerFactory.getLogger(URLProcessor.class);
    private static final Pattern URL_PATTERN = Pattern.compile("<a[^>]+href=[\"'](.*?)[\"']");

    private final String baseUrl;

    public URLProcessor(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void processUrl(String url, Crawl crawl, ThreadPoolManager threadPoolManager) {
        if (!crawl.getVisitedUrls().add(url)) {
            return; // Already visited
        }

        crawl.getActiveTasks().incrementAndGet();
        threadPoolManager.submitTask(() -> {
            try {
                URL targetUrl = new URL(url);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(targetUrl.openConnection().getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }

                String pageContent = content.toString().toLowerCase();
                if (pageContent.contains(crawl.getKeyword().toLowerCase())) {
                    crawl.addUrl(url);
                }

                Matcher matcher = URL_PATTERN.matcher(pageContent);
                while (matcher.find()) {
                    String foundUrl = matcher.group(1);
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