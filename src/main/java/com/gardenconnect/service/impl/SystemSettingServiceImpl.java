package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.config.AppConfig;
import com.gardenconnect.dao.SystemSettingDAO;
import com.gardenconnect.dao.impl.SystemSettingDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.SystemSetting;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.service.SystemSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for system configuration management.
 */
public class SystemSettingServiceImpl implements SystemSettingService {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingServiceImpl.class);

    private final SystemSettingDAO settingDAO;
    private final AsyncActivityProcessor asyncActivityProcessor;

    public SystemSettingServiceImpl() {
        this(new SystemSettingDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public SystemSettingServiceImpl(SystemSettingDAO settingDAO, AsyncActivityProcessor asyncActivityProcessor) {
        this.settingDAO = settingDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public Map<String, SystemSetting> getAllSettings() throws GardenConnectException {
        return settingDAO.findAll();
    }

    @Override
    public String getSettingValue(String key, String defaultValue) {
        try {
            Optional<SystemSetting> setting = settingDAO.findByKey(key);
            return setting.map(SystemSetting::getValue).orElse(defaultValue);
        } catch (Exception e) {
            logger.warn("Could not retrieve system setting {}: {}", key, e.getMessage());
            return defaultValue;
        }
    }

    @Override
    public boolean updateSetting(Long adminId, String adminUsername, String key, String value) throws GardenConnectException {
        if (key == null || key.trim().isEmpty()) {
            throw new ValidationException("Setting key cannot be empty.");
        }

        String sanitizedValue = InputSanitizer.escapeHtml(value != null ? value.trim() : "");
        boolean updated = settingDAO.updateValue(key, sanitizedValue);
        if (updated) {
            logger.info("Admin {} updated setting {} to '{}'", adminUsername, key, sanitizedValue);
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    adminUsername,
                    "SETTING_UPDATED",
                    "SETTING",
                    null,
                    "Updated setting '" + key + "' = '" + sanitizedValue + "'",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean isMaintenanceMode() {
        String val = getSettingValue(AppConfig.SETTING_MAINTENANCE_MODE, "false");
        return Boolean.parseBoolean(val);
    }

    @Override
    public boolean isRegistrationOpen() {
        String val = getSettingValue(AppConfig.SETTING_REGISTRATION_OPEN, "true");
        return Boolean.parseBoolean(val);
    }

    @Override
    public int getMaxUploadSizeMb() {
        String val = getSettingValue(AppConfig.SETTING_MAX_UPLOAD_MB, "5");
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return AppConfig.DEFAULT_MAX_UPLOAD_MB;
        }
    }
}
