package com.gardenconnect.concurrency;

import com.gardenconnect.model.ActivityLog;

/**
 * Interface defining asynchronous activity processing and lifecycle operations.
 * Demonstrates Abstraction and Dependency Inversion Principle (DIP).
 */
public interface ActivityProcessor {

    /**
     * Submits an activity log entry asynchronously without blocking user HTTP requests.
     *
     * @param log The audit log entry to record.
     * @return true if accepted into processing queue, false if dropped.
     */
    boolean submitLog(ActivityLog log);

    /**
     * Gracefully stops the worker threads and flushes remaining queue items.
     */
    void shutdown();

    int getQueueSize();

    int getTotalEnqueued();

    int getTotalPersisted();

    int getTotalDropped();
}
