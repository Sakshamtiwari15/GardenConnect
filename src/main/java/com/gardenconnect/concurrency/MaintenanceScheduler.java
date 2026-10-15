package com.gardenconnect.concurrency;

import com.gardenconnect.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled background executor for periodic health checks, metric flushes, and system maintenance.
 * Demonstrates Java ScheduledExecutorService and lifecycle control.
 */
public class MaintenanceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceScheduler.class);
    private static volatile MaintenanceScheduler instance;
    private static final Object LOCK = new Object();

    private final ScheduledExecutorService scheduler;

    public MaintenanceScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "GardenConnect-Maintenance");
            t.setDaemon(true);
            return t;
        });
    }

    public static MaintenanceScheduler getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new MaintenanceScheduler();
                }
            }
        }
        return instance;
    }

    /**
     * Starts periodic scheduled tasks.
     */
    public void start() {
        logger.info("Starting GardenConnect Maintenance Scheduler...");

        // Task 1: Periodic database ping & connection health logging every 10 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                boolean healthy = DatabaseConfig.isHealthy();
                logger.debug("Scheduled maintenance health check: Database connection healthy = {}", healthy);
            } catch (Exception e) {
                logger.warn("Scheduled maintenance check encountered error: {}", e.getMessage());
            }
        }, 1, 10, TimeUnit.MINUTES);
    }

    /**
     * Gracefully terminates the scheduler.
     */
    public void shutdown() {
        logger.info("Stopping GardenConnect Maintenance Scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
