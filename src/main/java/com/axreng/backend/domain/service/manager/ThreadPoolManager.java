package com.axreng.backend.domain.service.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
    private static final int MAX_THREADS = 20;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 60;

    private final ExecutorService executor;

    public ThreadPoolManager() {
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void submitTask(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        logger.info("Shutting down thread pool");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                logger.warn("Forcing thread pool shutdown after timeout");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread pool shutdown interrupted", e);
            executor.shutdownNow();
        }
    }
} 