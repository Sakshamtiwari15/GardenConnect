package com.gardenconnect.concurrency;

import com.gardenconnect.dao.ActivityLogDAO;
import com.gardenconnect.model.ActivityLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AsyncActivityProcessor Concurrency & Multithreading Tests")
class AsyncActivityProcessorTest {

    @Mock
    private ActivityLogDAO activityLogDAO;

    @Test
    @DisplayName("Submit log enqueues task and updates atomic counters and concurrent action map")
    void testSubmitLogCountersAndMap() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);

        when(activityLogDAO.create(any(ActivityLog.class))).thenAnswer(invocation -> {
            latch.countDown();
            return invocation.getArgument(0);
        });

        AsyncActivityProcessor processor = new AsyncActivityProcessor(activityLogDAO);

        try {
            ActivityLog log1 = new ActivityLog(1L, "gardener1", "USER_LOGIN", "USER", 1L, "Logged in", "127.0.0.1");
            ActivityLog log2 = new ActivityLog(1L, "gardener1", "TIP_CREATE", "TIP", 10L, "Created tip", "127.0.0.1");
            ActivityLog log3 = new ActivityLog(2L, "admin", "TIP_CREATE", "TIP", 11L, "Created tip", "127.0.0.1");

            assertTrue(processor.submitLog(log1));
            assertTrue(processor.submitLog(log2));
            assertTrue(processor.submitLog(log3));

            assertEquals(3, processor.getTotalEnqueued(), "Total enqueued should increment atomically to 3");
            assertEquals(1, processor.getActionCounterMap().get("USER_LOGIN").get());
            assertEquals(2, processor.getActionCounterMap().get("TIP_CREATE").get());

            // Wait for worker thread to process all 3 items asynchronously
            boolean processedAll = latch.await(3, TimeUnit.SECONDS);
            assertTrue(processedAll, "Background worker thread should process logs within timeout");
            assertEquals(3, processor.getTotalPersisted(), "Total persisted should match 3");
            verify(activityLogDAO, times(3)).create(any(ActivityLog.class));
        } finally {
            processor.shutdown();
        }
    }

    @Test
    @DisplayName("Graceful shutdown prevents new submissions and shuts down executor cleanly")
    void testGracefulShutdown() {
        AsyncActivityProcessor processor = new AsyncActivityProcessor(activityLogDAO);
        processor.shutdown();

        ActivityLog postShutdownLog = new ActivityLog(1L, "user", "TEST", "TEST", 1L, "Test", "127.0.0.1");
        boolean accepted = processor.submitLog(postShutdownLog);

        assertFalse(accepted, "Submissions must be rejected after processor is shut down");
        assertTrue(processor.getTotalDropped() >= 1, "Dropped count should increment when rejected post-shutdown");
    }
}
