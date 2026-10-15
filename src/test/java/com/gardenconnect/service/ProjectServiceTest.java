package com.gardenconnect.service;

import com.gardenconnect.concurrency.ActivityProcessor;
import com.gardenconnect.dao.GardeningProjectDAO;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.UnauthorizedActionException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;
import com.gardenconnect.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Business Logic Tests")
class ProjectServiceTest {

    @Mock
    private GardeningProjectDAO projectDAO;

    @Mock
    private ActivityProcessor asyncActivityProcessor;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectDAO, asyncActivityProcessor);
    }

    @Test
    @DisplayName("Create project successfully adjusts status to COMPLETED when progress is 100%")
    void testCreateProjectAutoCompletedWhen100Percent() throws Exception {
        when(projectDAO.create(any(GardeningProject.class))).thenAnswer(invocation -> {
            GardeningProject p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        GardeningProject result = projectService.createProject(
                1L, "sarah_green", "Spring Heirloom Tomatoes",
                "Growing Brandywine and Cherokee Purple", "Vegetables",
                Date.valueOf("2026-03-01"), Date.valueOf("2026-06-30"),
                ProjectStatus.IN_PROGRESS, 100, "All harvested!"
        );

        assertNotNull(result);
        assertEquals(ProjectStatus.COMPLETED, result.getStatus(), "Status should auto-complete to COMPLETED when progress is 100%");
        verify(projectDAO, times(1)).create(any(GardeningProject.class));
        verify(asyncActivityProcessor, times(1)).submitLog(any());
    }

    @Test
    @DisplayName("Create project throws ValidationException when progress is out of bounds")
    void testCreateProjectInvalidProgress() {
        assertThrows(ValidationException.class, () -> {
            projectService.createProject(
                    1L, "sarah_green", "Invalid Progress Project",
                    "Description", "Herbs",
                    Date.valueOf("2026-03-01"), Date.valueOf("2026-06-30"),
                    ProjectStatus.IN_PROGRESS, 150, "Invalid notes"
            );
        }, "Progress > 100 must throw ValidationException");

        assertThrows(ValidationException.class, () -> {
            projectService.createProject(
                    1L, "sarah_green", "Invalid Negative Progress",
                    "Description", "Herbs",
                    Date.valueOf("2026-03-01"), Date.valueOf("2026-06-30"),
                    ProjectStatus.IN_PROGRESS, -10, "Invalid notes"
            );
        }, "Negative progress must throw ValidationException");
    }

    @Test
    @DisplayName("Enforce ownership: access denied when user attempts to access another user's project")
    void testEnforceOwnershipAccessDenied() throws Exception {
        GardeningProject foreignProject = new GardeningProject();
        foreignProject.setId(50L);
        foreignProject.setUserId(999L); // Belongs to different gardener
        foreignProject.setTitle("Someone Else's Bonsai");

        when(projectDAO.findById(50L)).thenReturn(Optional.of(foreignProject));

        assertThrows(UnauthorizedActionException.class, () -> {
            projectService.getProject(50L, 1L); // User 1 trying to access user 999's project
        });
    }

    @Test
    @DisplayName("ResourceNotFoundException thrown when project does not exist")
    void testProjectNotFound() throws Exception {
        when(projectDAO.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProject(9999L, 1L);
        });
    }

    @Test
    @DisplayName("Update progress to 100% automatically sets status to COMPLETED")
    void testUpdateProgressTo100Percent() throws Exception {
        GardeningProject existing = new GardeningProject();
        existing.setId(5L);
        existing.setUserId(1L);
        existing.setTitle("Rose Garden");
        existing.setProgressPercentage(75);
        existing.setStatus(ProjectStatus.IN_PROGRESS);

        when(projectDAO.findById(5L)).thenReturn(Optional.of(existing));
        when(projectDAO.updateProgress(eq(5L), eq(1L), eq(100), eq(ProjectStatus.COMPLETED))).thenReturn(true);

        boolean updated = projectService.updateProgress(5L, 1L, "sarah_green", 100, ProjectStatus.IN_PROGRESS);

        assertTrue(updated);
        verify(projectDAO).updateProgress(5L, 1L, 100, ProjectStatus.COMPLETED);
    }
}
