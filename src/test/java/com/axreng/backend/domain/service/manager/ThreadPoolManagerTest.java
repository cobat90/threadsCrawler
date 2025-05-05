package com.axreng.backend.domain.service.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ThreadPoolManagerTest {

    private ThreadPoolManager threadPoolManager;

    @BeforeEach
    void setUp() {
        threadPoolManager = new ThreadPoolManager();
    }

    @Test
    void testSubmitTask() {
        // Arrange
        final boolean[] taskExecuted = {false};
        Runnable task = () -> taskExecuted[0] = true;

        // Act
        threadPoolManager.submitTask(task);

        // Assert
        try {
            Thread.sleep(100); // Give time for the task to execute
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertTrue(taskExecuted[0]);
    }

    @Test
    void testShutdown() {
        // Act
        threadPoolManager.shutdown();

        // Assert
        // No exception should be thrown
        assertTrue(true);
    }
} 