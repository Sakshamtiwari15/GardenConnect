package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.ActivityLogDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.ActivityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ActivityLogDAO.
 */
public class ActivityLogDAOImpl implements ActivityLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT id, user_id, username, action, entity_type, entity_id, details, ip_address, created_at " +
            "FROM activity_logs ";

    @Override
    public Optional<ActivityLog> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLog(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding activity log by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find activity log", e);
        }
    }

    @Override
    public ActivityLog create(ActivityLog log) throws DatabaseOperationException {
        String sql = "INSERT INTO activity_logs (user_id, username, action, entity_type, entity_id, details, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) {
                stmt.setLong(1, log.getUserId());
            } else {
                stmt.setNull(1, Types.BIGINT);
            }
            stmt.setString(2, log.getUsername());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getEntityType());
            if (log.getEntityId() != null) {
                stmt.setLong(5, log.getEntityId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            stmt.setString(6, log.getDetails());
            stmt.setString(7, log.getIpAddress());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseOperationException("Creating activity log failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    log.setId(generatedKeys.getLong(1));
                }
            }
            return log;
        } catch (SQLException e) {
            logger.error("Error saving activity log: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to persist activity log", e);
        }
    }

    @Override
    public boolean update(ActivityLog entity) throws DatabaseOperationException {
        // Logs are immutable append-only audit records
        return false;
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM activity_logs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting activity log {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete activity log", e);
        }
    }

    @Override
    public List<ActivityLog> findAll(int offset, int limit) throws DatabaseOperationException {
        return findAll(offset, limit, null);
    }

    @Override
    public List<ActivityLog> findAll(int offset, int limit, String actionFilter) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();

        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            sql.append("WHERE action = ? ");
            params.add(actionFilter.trim());
        }
        sql.append("ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving activity logs: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve activity logs", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        return count(null);
    }

    @Override
    public long count(String actionFilter) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM activity_logs ");
        List<Object> params = new ArrayList<>();
        if (actionFilter != null && !actionFilter.trim().isEmpty()) {
            sql.append("WHERE action = ? ");
            params.add(actionFilter.trim());
        }
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting activity logs: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count activity logs", e);
        }
    }

    @Override
    public List<ActivityLog> findRecent(int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "ORDER BY created_at DESC LIMIT ?";
        List<ActivityLog> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding recent activity logs: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find recent activity logs", e);
        }
    }

    private ActivityLog mapResultSetToLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getLong("id"));
        long userId = rs.getLong("user_id");
        if (!rs.wasNull()) {
            log.setUserId(userId);
        }
        log.setUsername(rs.getString("username"));
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        long entityId = rs.getLong("entity_id");
        if (!rs.wasNull()) {
            log.setEntityId(entityId);
        }
        log.setDetails(rs.getString("details"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
