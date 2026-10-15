package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;
import com.gardenconnect.util.PaginationUtil;

import java.sql.Date;
import java.util.List;

/**
 * Service managing gardener personal project lifecycles, milestones, and progress updates.
 */
public interface ProjectService {

    GardeningProject createProject(Long userId, String username, String title, String description,
                                   String plantCropType, Date startDate, Date targetDate,
                                   ProjectStatus status, int progressPercentage, String notes) throws GardenConnectException;

    boolean updateProject(Long projectId, Long userId, String username, String title, String description,
                          String plantCropType, Date startDate, Date targetDate,
                          ProjectStatus status, int progressPercentage, String notes) throws GardenConnectException;

    boolean updateProgress(Long projectId, Long userId, String username, int progressPercentage, ProjectStatus status) throws GardenConnectException;

    boolean deleteProject(Long projectId, Long userId, String username) throws GardenConnectException;

    GardeningProject getProject(Long projectId, Long userId) throws GardenConnectException;

    PaginationUtil<GardeningProject> getUserProjects(Long userId, int page, int pageSize, ProjectStatus statusFilter) throws GardenConnectException;

    List<GardeningProject> getRecentProjects(Long userId, int limit) throws GardenConnectException;
}
