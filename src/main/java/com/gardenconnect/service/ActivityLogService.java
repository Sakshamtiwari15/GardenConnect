package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.util.PaginationUtil;

import java.util.List;

/**
 * Service managing audit trail logging and inspection.
 */
public interface ActivityLogService {

    void logAsync(Long userId, String username, String action, String entityType, Long entityId, String details, String ipAddress);

    List<ActivityLog> getRecentLogs(int limit) throws GardenConnectException;

    PaginationUtil<ActivityLog> getLogs(int page, int pageSize, String actionFilter) throws GardenConnectException;
}
