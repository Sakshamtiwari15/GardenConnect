package com.gardenconnect.controller.gardener;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;
import com.gardenconnect.model.User;
import com.gardenconnect.service.ProjectService;
import com.gardenconnect.service.impl.ProjectServiceImpl;
import com.gardenconnect.util.DateTimeUtil;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;

/**
 * Controller for managing personal gardening projects, progress sliders (0-100%), and status lifecycles.
 */
@WebServlet(name = "GardenerProjectController", urlPatterns = {
        "/gardener/projects",
        "/gardener/projects/create",
        "/gardener/projects/edit",
        "/gardener/projects/progress",
        "/gardener/projects/delete"
})
public class GardenerProjectController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GardenerProjectController.class);

    private final ProjectService projectService;

    public GardenerProjectController() {
        this.projectService = new ProjectServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        User user = getAuthenticatedUser(request);
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        switch (path) {
            case "/gardener/projects/create":
                showCreateForm(request, response);
                break;
            case "/gardener/projects/edit":
                showEditForm(request, response, user.getId());
                break;
            case "/gardener/projects":
            default:
                listGardenerProjects(request, response, user.getId());
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        User user = getAuthenticatedUser(request);
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        switch (path) {
            case "/gardener/projects/create":
                handleCreateProject(request, response, user);
                break;
            case "/gardener/projects/edit":
                handleUpdateProject(request, response, user);
                break;
            case "/gardener/projects/progress":
                handleUpdateProgress(request, response, user);
                break;
            case "/gardener/projects/delete":
                handleDeleteProject(request, response, user);
                break;
            default:
                sendNotFound(request, response, "Page not found");
        }
    }

    private void listGardenerProjects(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), AppConfig.DEFAULT_PAGE_SIZE);

            String statusStr = request.getParameter("status");
            ProjectStatus statusFilter = (statusStr != null && !statusStr.isEmpty()) ? ProjectStatus.fromString(statusStr) : null;

            PaginationUtil<GardeningProject> pageResult = projectService.getUserProjects(userId, page, pageSize, statusFilter);

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("selectedStatus", statusFilter);
            request.setAttribute("statuses", ProjectStatus.values());
            request.setAttribute("pageTitle", "My Gardening Projects - GardenConnect");
            request.setAttribute("activeTab", "projects");

            forward(request, response, "gardener/projects");
        } catch (Exception e) {
            logger.error("Error listing projects for user {}: {}", userId, e.getMessage(), e);
            sendInternalError(request, response, "Unable to load your gardening projects.");
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("statuses", ProjectStatus.values());
        request.setAttribute("pageTitle", "Start a Gardening Project - GardenConnect");
        request.setAttribute("activeTab", "projects");
        forward(request, response, "gardener/project-form");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            sendNotFound(request, response, "Invalid project ID.");
            return;
        }

        try {
            GardeningProject project = projectService.getProject(id, userId);
            request.setAttribute("project", project);
            request.setAttribute("statuses", ProjectStatus.values());
            request.setAttribute("pageTitle", "Edit Gardening Project - GardenConnect");
            request.setAttribute("activeTab", "projects");
            forward(request, response, "gardener/project-form");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/projects");
        }
    }

    private void handleCreateProject(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String plantCropType = request.getParameter("plantCropType");
        Date startDate = DateTimeUtil.parseSqlDate(request.getParameter("startDate"));
        Date targetDate = DateTimeUtil.parseSqlDate(request.getParameter("targetCompletionDate"));
        ProjectStatus status = ProjectStatus.fromString(request.getParameter("status"));
        int progress = ValidationUtil.parsePositiveInt(request.getParameter("progressPercentage"), 0);
        String notes = request.getParameter("notes");

        try {
            projectService.createProject(user.getId(), user.getUsername(), title, description,
                    plantCropType, startDate, targetDate, status, progress, notes);
            setFlashSuccess(request, "Gardening project created successfully!");
            redirect(request, response, "/gardener/projects");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute(AppConfig.REQ_ERROR_MAP, e.getFieldErrors());
            showCreateForm(request, response);
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/projects");
        }
    }

    private void handleUpdateProject(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            sendNotFound(request, response, "Invalid project ID.");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String plantCropType = request.getParameter("plantCropType");
        Date startDate = DateTimeUtil.parseSqlDate(request.getParameter("startDate"));
        Date targetDate = DateTimeUtil.parseSqlDate(request.getParameter("targetCompletionDate"));
        ProjectStatus status = ProjectStatus.fromString(request.getParameter("status"));
        int progress = ValidationUtil.parsePositiveInt(request.getParameter("progressPercentage"), 0);
        String notes = request.getParameter("notes");

        try {
            projectService.updateProject(id, user.getId(), user.getUsername(), title, description,
                    plantCropType, startDate, targetDate, status, progress, notes);
            setFlashSuccess(request, "Project details updated successfully.");
            redirect(request, response, "/gardener/projects");
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/projects/edit?id=" + id);
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/projects");
        }
    }

    private void handleUpdateProgress(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        int progress = ValidationUtil.parsePositiveInt(request.getParameter("progressPercentage"), 0);
        String statusStr = request.getParameter("status");
        ProjectStatus status = (statusStr != null && !statusStr.isEmpty()) ? ProjectStatus.fromString(statusStr) : null;

        if (id == null) {
            setFlashError(request, "Invalid project ID.");
            redirect(request, response, "/gardener/projects");
            return;
        }

        try {
            projectService.updateProgress(id, user.getId(), user.getUsername(), progress, status);
            setFlashSuccess(request, "Progress updated to " + progress + "%!");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/gardener/projects");
    }

    private void handleDeleteProject(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            setFlashError(request, "Invalid project ID.");
            redirect(request, response, "/gardener/projects");
            return;
        }

        try {
            projectService.deleteProject(id, user.getId(), user.getUsername());
            setFlashSuccess(request, "Gardening project removed.");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/gardener/projects");
    }
}
