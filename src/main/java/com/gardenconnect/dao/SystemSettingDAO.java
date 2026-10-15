package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.SystemSetting;

import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for database-backed system settings.
 */
public interface SystemSettingDAO {

    Map<String, SystemSetting> findAll() throws DatabaseOperationException;

    Optional<SystemSetting> findByKey(String key) throws DatabaseOperationException;

    boolean saveOrUpdate(String key, String value, String description, String dataType) throws DatabaseOperationException;

    boolean updateValue(String key, String value) throws DatabaseOperationException;
}
