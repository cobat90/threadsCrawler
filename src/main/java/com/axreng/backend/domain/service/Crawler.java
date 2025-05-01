package com.axreng.backend.domain.service;

import com.axreng.backend.domain.model.Crawl;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    private static final Pattern URL_PATTERN = Pattern.compile("<a[^>]+href=[\"'](.*?)[\"']");
    private final String baseUrl;

    public Crawler(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void crawl(Crawl crawl) {
        crawlUrl(baseUrl, crawl, ConcurrentHashMap.newKeySet());
    }

    private void crawlUrl(String url, Crawl crawl, Set<String> visitedUrls) {
        if (visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);

        try {
            URL targetUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(targetUrl.openStream()));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String pageContent = content.toString();
            if (pageContent.toLowerCase().contains(crawl.getKeyword().toLowerCase())) {
                crawl.addUrl(url);
            }

            Matcher matcher = URL_PATTERN.matcher(pageContent);
            while (matcher.find()) {
                String foundUrl = matcher.group(1);
                String absoluteUrl = resolveUrl(foundUrl);
                
                if (absoluteUrl != null && 
                    absoluteUrl.startsWith(baseUrl) && 
                    !visitedUrls.contains(absoluteUrl) &&
                    !absoluteUrl.contains("../") &&
                    !absoluteUrl.contains("javascript:") &&
                    !absoluteUrl.contains("mailto:") &&
                    !absoluteUrl.contains("#")) {
                    crawlUrl(absoluteUrl, crawl, visitedUrls);
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Error crawling URL: " + url + " - " + e.getMessage());
        }
    }

    private String resolveUrl(String foundUrl) {
        try {
            if (foundUrl == null || foundUrl.isEmpty() || 
                foundUrl.startsWith("javascript:") || 
                foundUrl.startsWith("mailto:") || 
                foundUrl.startsWith("#")) {
                return null;
            }

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