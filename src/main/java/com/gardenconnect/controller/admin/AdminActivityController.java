package com.gardenconnect.controller.admin;

import com.gardenconnect.controller.BaseController;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.service.ActivityLogService;
import com.gardenconnect.service.impl.ActivityLogServiceImpl;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for inspecting the system audit trail and user activity logs.
 */
@WebServlet(name = "AdminActivityController", urlPatterns = "/admin/activity")
public class AdminActivityController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminActivityController.class);

    private final ActivityLogService activityLogService;

    public AdminActivityController() {
        this.activityLogService = new ActivityLogServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), 15);
            String actionFilter = request.getParameter("action");

            PaginationUtil<ActivityLog> pageResult = activityLogService.getLogs(page, pageSize, actionFilter);

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("actionFilter", actionFilter);
            request.setAttribute("pageTitle", "Audit Activity Trail - GardenConnect Admin");
            request.setAttribute("activeTab", "activity");

            forward(request, response, "admin/activity");
        } catch (Exception e) {
            logger.error("Error loading activity logs: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load activity logs.");
        }
    }
}
