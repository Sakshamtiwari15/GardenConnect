package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.SystemSettingDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.SystemSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC implementation of SystemSettingDAO.
 */
public class SystemSettingDAOImpl implements SystemSettingDAO {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT setting_key, setting_value, description, data_type, updated_at FROM system_settings ";

    @Override
    public Map<String, SystemSetting> findAll() throws DatabaseOperationException {
        String sql = SELECT_BASE + "ORDER BY setting_key ASC";
        Map<String, SystemSetting> map = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                SystemSetting s = mapResultSetToSetting(rs);
                map.put(s.getKey(), s);
            }
            return map;
        } catch (SQLException e) {
            logger.error("Error retrieving system settings: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve system settings", e);
        }
    }

    @Override
    public Optional<SystemSetting> findByKey(String key) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE setting_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSetting(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding system setting by key {}: {}", key, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find system setting", e);
        }
    }

    @Override
    public boolean saveOrUpdate(String key, String value, String description, String dataType) throws DatabaseOperationException {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, description, data_type) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), description = VALUES(description), data_type = VALUES(data_type)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setString(3, description);
            stmt.setString(4, dataType);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error saving system setting {}: {}", key, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save system setting", e);
        }
    }

    @Override
    public boolean updateValue(String key, String value) throws DatabaseOperationException {
        String sql = "UPDATE system_settings SET setting_value = ? WHERE setting_key = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, key);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating system setting {}: {}", key, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update system setting", e);
        }
    }

    private SystemSetting mapResultSetToSetting(ResultSet rs) throws SQLException {
        SystemSetting s = new SystemSetting();
        s.setKey(rs.getString("setting_key"));
        s.setValue(rs.getString("setting_value"));
        s.setDescription(rs.getString("description"));
        s.setDataType(rs.getString("data_type"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
