package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object for personal gardening projects.
 */
public interface GardeningProjectDAO extends BaseDAO<GardeningProject, Long> {

    List<GardeningProject> findByUserId(Long userId, int offset, int limit, ProjectStatus statusFilter) throws DatabaseOperationException;

    long countByUserId(Long userId, ProjectStatus statusFilter) throws DatabaseOperationException;

    boolean updateProgress(Long id, Long userId, int progress, ProjectStatus status) throws DatabaseOperationException;

    boolean delete(Long id, Long userId) throws DatabaseOperationException;

    List<GardeningProject> findRecentByUserId(Long userId, int limit) throws DatabaseOperationException;

    Map<String, Long> getProjectCountByStatus() throws DatabaseOperationException;
}
