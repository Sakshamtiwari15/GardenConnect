package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.SystemSetting;

import java.util.Map;

/**
 * Service managing dynamic platform settings stored in MySQL.
 */
public interface SystemSettingService {

    Map<String, SystemSetting> getAllSettings() throws GardenConnectException;

    String getSettingValue(String key, String defaultValue);

    boolean updateSetting(Long adminId, String adminUsername, String key, String value) throws GardenConnectException;

    boolean isMaintenanceMode();

    boolean isRegistrationOpen();

    int getMaxUploadSizeMb();
}
