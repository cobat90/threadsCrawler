package com.axreng.backend.domain.service;

import com.axreng.backend.domain.model.Crawl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    private static final Pattern URL_PATTERN = Pattern.compile("<a[^>]+href=[\"'](.*?)[\"']");
    private static final int MAX_THREADS = 20;
    private static final int TIMEOUT_MS = 5000;

    private final String baseUrl;
    private final ExecutorService executor;

    public Crawler(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void crawl(Crawl crawl) {
        crawl.getVisitedUrls().clear(); // Clear visited URLs for new crawl
        crawl.getActiveTasks().set(0); // Reset active tasks counter

        submitTask(baseUrl, crawl);
    }

    private void submitTask(String url, Crawl crawl) {
        if (!crawl.getVisitedUrls().add(url)) {
            return; // Already visited
        }

        crawl.getActiveTasks().incrementAndGet();
        executor.submit(() -> {
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
                        submitTask(absoluteUrl, crawl);
                    }
                }

                reader.close();
            } catch (Exception e) {
                System.err.println("Error crawling URL: " + url + " - " + e.getMessage());
            } finally {
                int remainingTasks = crawl.getActiveTasks().decrementAndGet();
                if (remainingTasks == 0) {
                    // Only count down the latch if we're sure all tasks are done
                    synchronized (crawl) {
                        if (crawl.getActiveTasks().get() == 0) {
                            crawl.getCompletionLatch().countDown();
                        }
                    }
                }
            }
        });
    }

    private boolean shouldVisit(String url) {
        return url != null
                && url.startsWith(baseUrl)
                && !url.contains("../")
                && !url.contains("javascript:")
                && !url.contains("mailto:")
                && !url.contains("#");
    }

    private String resolveUrl(String foundUrl) {
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
            System.err.println("Error resolving URL: " + foundUrl + " - " + e.getMessage());
            return null;
        }
    }
}
