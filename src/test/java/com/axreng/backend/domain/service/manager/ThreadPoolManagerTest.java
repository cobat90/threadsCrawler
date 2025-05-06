package com.axreng.backend.domain.service.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadPoolManagerTest {

    private ThreadPoolManager threadPoolManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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