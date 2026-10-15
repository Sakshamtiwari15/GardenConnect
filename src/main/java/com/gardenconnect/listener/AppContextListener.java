package com.gardenconnect.listener;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.concurrency.MaintenanceScheduler;
import com.gardenconnect.config.AppConfig;
import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.model.SystemSetting;
import com.gardenconnect.service.SystemSettingService;
import com.gardenconnect.service.impl.SystemSettingServiceImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;

/**
 * Application lifecycle listener.
 * Responsible for initializing database pools, asynchronous background workers,
 * scheduled jobs, and cleaning up resources on Tomcat undeployment.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        logger.info("Initializing GardenConnect application context on Tomcat 10.1...");

        try {
            // 1. Initialize Database Connection Pool
            DatabaseConfig.initialize();

            // 2. Initialize and load dynamic System Settings into application context
            SystemSettingService settingService = new SystemSettingServiceImpl();
            try {
                Map<String, SystemSetting> settings = settingService.getAllSettings();
                context.setAttribute(AppConfig.APP_SETTINGS, settings);

                String communityName = settingService.getSettingValue(AppConfig.SETTING_COMMUNITY_NAME, "GardenConnect Community");
                context.setAttribute(AppConfig.APP_COMMUNITY_NAME, communityName);
                logger.info("Loaded {} system settings into application context.", settings.size());
            } catch (Exception e) {
                logger.warn("Could not load initial system settings from database: {}. Using default constants.", e.getMessage());
                context.setAttribute(AppConfig.APP_COMMUNITY_NAME, "GardenConnect Community");
            }

            // 3. Start Asynchronous Activity Logger
            AsyncActivityProcessor.getInstance();
            logger.info("AsyncActivityProcessor initialized and running.");

            // 4. Start Maintenance Scheduler
            MaintenanceScheduler.getInstance().start();
            logger.info("MaintenanceScheduler background service started.");

            logger.info("GardenConnect platform started successfully!");
        } catch (Exception e) {
            logger.error("Error during application context initialization: {}", e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down GardenConnect application context...");

        try {
            // 1. Stop background maintenance scheduler
            MaintenanceScheduler.getInstance().shutdown();

            // 2. Flush and terminate asynchronous activity worker
            AsyncActivityProcessor.getInstance().shutdown();

            // 3. Close HikariCP connection pool
            DatabaseConfig.shutdown();

            // 4. Deregister JDBC drivers to prevent Tomcat memory leaks
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    logger.debug("Deregistered JDBC driver: {}", driver);
                } catch (Exception e) {
                    logger.warn("Error deregistering JDBC driver {}: {}", driver, e.getMessage());
                }
            }

            logger.info("GardenConnect shutdown and cleanup completed gracefully.");
        } catch (Exception e) {
            logger.error("Error during application context teardown: {}", e.getMessage(), e);
        }
    }
}
