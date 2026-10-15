package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.ActivityProcessor;
import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.GardeningProjectDAO;
import com.gardenconnect.dao.impl.GardeningProjectDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.UnauthorizedActionException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.service.ProjectService;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for personal gardening projects.
 */
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final GardeningProjectDAO projectDAO;
    private final ActivityProcessor asyncActivityProcessor;

    public ProjectServiceImpl() {
        this(new GardeningProjectDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public ProjectServiceImpl(GardeningProjectDAO projectDAO, ActivityProcessor asyncActivityProcessor) {
        this.projectDAO = projectDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public GardeningProject createProject(Long userId, String username, String title, String description,
                                          String plantCropType, Date startDate, Date targetDate,
                                          ProjectStatus status, int progressPercentage, String notes) throws GardenConnectException {
        validateProjectInput(title, progressPercentage, startDate, targetDate);

        // Auto-status adjustment if 100% complete
        if (progressPercentage == 100 && status != ProjectStatus.COMPLETED) {
            status = ProjectStatus.COMPLETED;
        }

        GardeningProject project = new GardeningProject();
        project.setUserId(userId);
        project.setTitle(InputSanitizer.escapeHtml(title.trim()));
        project.setDescription(InputSanitizer.escapeHtml(description != null ? description.trim() : null));
        project.setPlantCropType(InputSanitizer.escapeHtml(plantCropType != null ? plantCropType.trim() : null));
        project.setStartDate(startDate);
        project.setTargetCompletionDate(targetDate);
        project.setStatus(status != null ? status : ProjectStatus.PLANNED);
        project.setProgressPercentage(progressPercentage);
        project.setNotes(InputSanitizer.escapeHtml(notes != null ? notes.trim() : null));

        GardeningProject created = projectDAO.create(project);
        logger.info("Created gardening project ID: {} for user: {}", created.getId(), userId);

        asyncActivityProcessor.submitLog(new ActivityLog(
                userId,
                username,
                "PROJECT_CREATED",
                "PROJECT",
                created.getId(),
                "Started project: " + created.getTitle(),
                "127.0.0.1"
        ));

        return created;
    }

    @Override
    public boolean updateProject(Long projectId, Long userId, String username, String title, String description,
                                 String plantCropType, Date startDate, Date targetDate,
                                 ProjectStatus status, int progressPercentage, String notes) throws GardenConnectException {
        GardeningProject existing = getProject(projectId, userId);

        validateProjectInput(title, progressPercentage, startDate, targetDate);

        if (progressPercentage == 100 && status != ProjectStatus.COMPLETED) {
            status = ProjectStatus.COMPLETED;
        }

        existing.setTitle(InputSanitizer.escapeHtml(title.trim()));
        existing.setDescription(InputSanitizer.escapeHtml(description != null ? description.trim() : null));
        existing.setPlantCropType(InputSanitizer.escapeHtml(plantCropType != null ? plantCropType.trim() : null));
        existing.setStartDate(startDate);
        existing.setTargetCompletionDate(targetDate);
        existing.setStatus(status != null ? status : ProjectStatus.PLANNED);
        existing.setProgressPercentage(progressPercentage);
        existing.setNotes(InputSanitizer.escapeHtml(notes != null ? notes.trim() : null));

        boolean updated = projectDAO.update(existing);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    username,
                    "PROJECT_UPDATED",
                    "PROJECT",
                    projectId,
                    "Updated project: " + existing.getTitle() + " (" + progressPercentage + "%)",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean updateProgress(Long projectId, Long userId, String username, int progressPercentage, ProjectStatus status) throws GardenConnectException {
        GardeningProject existing = getProject(projectId, userId);

        if (!ValidationUtil.isValidPercentage(progressPercentage)) {
            throw new ValidationException("progressPercentage", "Progress must be between 0 and 100.");
        }

        if (progressPercentage == 100) {
            status = ProjectStatus.COMPLETED;
        } else if (status == null) {
            status = existing.getStatus();
        }

        boolean updated = projectDAO.updateProgress(projectId, userId, progressPercentage, status);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    username,
                    "PROJECT_PROGRESS_UPDATED",
                    "PROJECT",
                    projectId,
                    "Updated project progress to " + progressPercentage + "% (" + status.getDisplayName() + ")",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean deleteProject(Long projectId, Long userId, String username) throws GardenConnectException {
        GardeningProject existing = getProject(projectId, userId);

        boolean deleted = projectDAO.delete(projectId, userId);
        if (deleted) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    username,
                    "PROJECT_DELETED",
                    "PROJECT",
                    projectId,
                    "Deleted project: " + existing.getTitle(),
                    "127.0.0.1"
            ));
        }
        return deleted;
    }

    @Override
    public GardeningProject getProject(Long projectId, Long userId) throws GardenConnectException {
        GardeningProject project = projectDAO.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Project", projectId));

        if (!project.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only access your own personal gardening projects.");
        }
        return project;
    }

    @Override
    public PaginationUtil<GardeningProject> getUserProjects(Long userId, int page, int pageSize, ProjectStatus statusFilter) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<GardeningProject> items = projectDAO.findByUserId(userId, offset, pageSize, statusFilter);
        long total = projectDAO.countByUserId(userId, statusFilter);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public List<GardeningProject> getRecentProjects(Long userId, int limit) throws GardenConnectException {
        return projectDAO.findRecentByUserId(userId, limit);
    }

    private void validateProjectInput(String title, int progressPercentage, Date startDate, Date targetDate) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtil.isWithinLength(title, 3, 150)) {
            errors.put("title", "Project title must be between 3 and 150 characters.");
        }
        if (!ValidationUtil.isValidPercentage(progressPercentage)) {
            errors.put("progressPercentage", "Progress percentage must be between 0 and 100.");
        }
        if (startDate != null && targetDate != null && targetDate.before(startDate)) {
            errors.put("targetCompletionDate", "Target completion date cannot be before the start date.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Project validation failed", errors);
        }
    }
}
