package com.gardenconnect.concurrency;

import com.gardenconnect.dao.ActivityLogDAO;
import com.gardenconnect.dao.impl.ActivityLogDAOImpl;
import com.gardenconnect.model.ActivityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous background activity logger.
 * Demonstrates:
 * - Multithreading via ExecutorService and ThreadFactory
 * - Producer-Consumer concurrency pattern using bounded BlockingQueue
 * - Thread-safe synchronization via AtomicInteger and ConcurrentHashMap
 * - Graceful lifecycle management with shutdown hooks
 */
public class AsyncActivityProcessor implements ActivityProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncActivityProcessor.class);

    private static final int QUEUE_CAPACITY = 2000;
    private static volatile AsyncActivityProcessor instance;
    private static final Object INSTANCE_LOCK = new Object();

    private final BlockingQueue<ActivityLog> logQueue;
    private final ExecutorService executorService;
    private final ActivityLogDAO activityLogDAO;
    private final AtomicBoolean isRunning;

    // Concurrency metrics counters
    private final AtomicInteger totalEnqueued = new AtomicInteger(0);
    private final AtomicInteger totalPersisted = new AtomicInteger(0);
    private final AtomicInteger totalDropped = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> actionCounterMap = new ConcurrentHashMap<>();

    public AsyncActivityProcessor() {
        this(new ActivityLogDAOImpl());
    }

    public AsyncActivityProcessor(ActivityLogDAO activityLogDAO) {
        this.activityLogDAO = activityLogDAO;
        this.logQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.isRunning = new AtomicBoolean(true);

        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r, "GardenConnect-AsyncLogger");
            thread.setDaemon(true); // Allow JVM shutdown if needed
            return thread;
        };

        this.executorService = Executors.newSingleThreadExecutor(threadFactory);
        startWorker();
    }

    public static AsyncActivityProcessor getInstance() {
        if (instance == null) {
            synchronized (INSTANCE_LOCK) {
                if (instance == null) {
                    instance = new AsyncActivityProcessor();
                }
            }
        }
        return instance;
    }

    /**
     * Submits an activity log entry asynchronously without blocking user HTTP requests.
     */
    public boolean submitLog(ActivityLog log) {
        if (!isRunning.get()) {
            logger.warn("AsyncActivityProcessor is stopping; rejecting log: {}", log.getAction());
            totalDropped.incrementAndGet();
            return false;
        }

        boolean queued = logQueue.offer(log);
        if (queued) {
            totalEnqueued.incrementAndGet();
            // Track in-memory frequency atomically
            actionCounterMap.computeIfAbsent(log.getAction(), k -> new AtomicInteger(0)).incrementAndGet();
        } else {
            totalDropped.incrementAndGet();
            logger.warn("Log queue capacity ({}) exceeded. Dropping activity log: {}", QUEUE_CAPACITY, log.getAction());
        }
        return queued;
    }

    private void startWorker() {
        executorService.submit(() -> {
            logger.info("AsyncActivityProcessor background consumer thread started.");
            while (isRunning.get() || !logQueue.isEmpty()) {
                try {
                    ActivityLog log = logQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (log != null) {
                        activityLogDAO.create(log);
                        totalPersisted.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Worker thread interrupted during poll");
                    break;
                } catch (Exception e) {
                    logger.error("Error persisting async activity log: {}", e.getMessage(), e);
                }
            }
            logger.info("AsyncActivityProcessor worker loop terminated. Remaining in queue: {}", logQueue.size());
        });
    }

    /**
     * Graceful shutdown: signals stop, waits for existing queue to drain, terminates thread pool.
     */
    public void shutdown() {
        logger.info("Initiating graceful shutdown of AsyncActivityProcessor...");
        isRunning.set(false);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Worker did not finish in 5s, forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.info("AsyncActivityProcessor shutdown complete. Total enqueued: {}, persisted: {}, dropped: {}",
                totalEnqueued.get(), totalPersisted.get(), totalDropped.get());
    }

    // Monitoring and test accessors
    public int getQueueSize() {
        return logQueue.size();
    }

    public int getTotalEnqueued() {
        return totalEnqueued.get();
    }

    public int getTotalPersisted() {
        return totalPersisted.get();
    }

    public int getTotalDropped() {
        return totalDropped.get();
    }

    public Map<String, AtomicInteger> getActionCounterMap() {
        return Collections.unmodifiableMap(actionCounterMap);
    }
}
