package com.gardenconnect.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connectivity using HikariCP high-performance connection pool.
 * Configuration priority:
 * 1. Environment Variables (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD)
 * 2. db.properties file in classpath
 * 3. Default fallback values for local development
 */
public final class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static volatile HikariDataSource dataSource;
    private static final Object LOCK = new Object();

    private DatabaseConfig() {}

    /**
     * Initializes the connection pool singleton with thread-safe double-checked locking.
     */
    public static void initialize() {
        if (dataSource == null) {
            synchronized (LOCK) {
                if (dataSource == null) {
                    try {
                        HikariConfig config = new HikariConfig();
                        Properties props = loadProperties();

                        // Resolve Database Configuration with Environment Variable precedence
                        String host = getEnvOrProp("DB_HOST", "db.host", props, "localhost");
                        String port = getEnvOrProp("DB_PORT", "db.port", props, "3306");
                        String dbName = getEnvOrProp("DB_NAME", "db.name", props, "gardenconnect");
                        String user = getEnvOrProp("DB_USER", "db.username", props, "root");
                        String password = getEnvOrProp("DB_PASSWORD", "db.password", props, "");

                        String jdbcUrl = props.getProperty("db.url");
                        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
                            jdbcUrl = String.format(
                                    "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8",
                                    host, port, dbName
                            );
                        }

                        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                        config.setJdbcUrl(jdbcUrl);
                        config.setUsername(user);
                        config.setPassword(password);

                        // Pool sizing and timeouts
                        config.setMaximumPoolSize(Integer.parseInt(getEnvOrProp("DB_POOL_SIZE", "db.pool.maximumPoolSize", props, "10")));
                        config.setMinimumIdle(Integer.parseInt(getEnvOrProp("DB_MIN_IDLE", "db.pool.minimumIdle", props, "2")));
                        config.setConnectionTimeout(Long.parseLong(getEnvOrProp("DB_TIMEOUT_MS", "db.pool.connectionTimeoutMs", props, "30000")));
                        config.setIdleTimeout(600000); // 10 minutes
                        config.setMaxLifetime(1800000); // 30 minutes
                        config.setPoolName("GardenConnect-HikariPool");

                        // Connection optimization settings
                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                        config.addDataSourceProperty("useServerPrepStmts", "true");

                        dataSource = new HikariDataSource(config);
                        logger.info("HikariCP connection pool successfully initialized for {}", jdbcUrl);
                    } catch (Exception e) {
                        logger.error("Failed to initialize HikariCP connection pool: {}", e.getMessage(), e);
                        throw new RuntimeException("Database connection initialization failed", e);
                    }
                }
            }
        }
    }

    /**
     * Retrieves an active connection from the pool.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }

    /**
     * Verifies if the database is accessible (used for health checks).
     */
    public static boolean isHealthy() {
        if (dataSource == null) {
            try {
                initialize();
            } catch (Exception e) {
                return false;
            }
        }
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            logger.warn("Database health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gracefully shuts down the connection pool upon application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing HikariCP connection pool...");
            dataSource.close();
            dataSource = null;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                logger.debug("Loaded db.properties from classpath");
            }
        } catch (Exception e) {
            logger.debug("db.properties not found or unreadable, falling back to environment: {}", e.getMessage());
        }
        return props;
    }

    private static String getEnvOrProp(String envKey, String propKey, Properties props, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        String propVal = props.getProperty(propKey);
        if (propVal != null && !propVal.trim().isEmpty()) {
            return propVal.trim();
        }
        return defaultValue;
    }
}
