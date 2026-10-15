package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.ActivityLog;

import java.util.List;

/**
 * Data Access Object for audit activity logs.
 */
public interface ActivityLogDAO extends BaseDAO<ActivityLog, Long> {

    List<ActivityLog> findRecent(int limit) throws DatabaseOperationException;

    List<ActivityLog> findAll(int offset, int limit, String actionFilter) throws DatabaseOperationException;

    long count(String actionFilter) throws DatabaseOperationException;
}
