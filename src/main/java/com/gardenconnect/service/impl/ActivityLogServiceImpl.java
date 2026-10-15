package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.ActivityLogDAO;
import com.gardenconnect.dao.impl.ActivityLogDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.service.ActivityLogService;
import com.gardenconnect.util.PaginationUtil;

import java.util.List;

/**
 * Service implementation for audit logs using async processor and DAO retrieval.
 */
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogDAO logDAO;
    private final AsyncActivityProcessor asyncActivityProcessor;

    public ActivityLogServiceImpl() {
        this(new ActivityLogDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public ActivityLogServiceImpl(ActivityLogDAO logDAO, AsyncActivityProcessor asyncActivityProcessor) {
        this.logDAO = logDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public void logAsync(Long userId, String username, String action, String entityType, Long entityId, String details, String ipAddress) {
        ActivityLog log = new ActivityLog(userId, username, action, entityType, entityId, details, ipAddress);
        asyncActivityProcessor.submitLog(log);
    }

    @Override
    public List<ActivityLog> getRecentLogs(int limit) throws GardenConnectException {
        return logDAO.findRecent(limit);
    }

    @Override
    public PaginationUtil<ActivityLog> getLogs(int page, int pageSize, String actionFilter) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<ActivityLog> items = logDAO.findAll(offset, pageSize, actionFilter);
        long total = logDAO.count(actionFilter);
        return new PaginationUtil<>(items, page, pageSize, total);
    }
}
