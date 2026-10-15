package com.gardenconnect.controller.admin;

import com.gardenconnect.controller.BaseController;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.DashboardStats;
import com.gardenconnect.service.ActivityLogService;
import com.gardenconnect.service.DashboardService;
import com.gardenconnect.service.impl.ActivityLogServiceImpl;
import com.gardenconnect.service.impl.DashboardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Administrator Overview Dashboard.
 * Delivers real-time relational database analytics, moderation alerts, and Chart.js metrics.
 */
@WebServlet(name = "AdminDashboardController", urlPatterns = "/admin/dashboard")
public class AdminDashboardController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    private final DashboardService dashboardService;
    private final ActivityLogService activityLogService;

    public AdminDashboardController() {
        this.dashboardService = new DashboardServiceImpl();
        this.activityLogService = new ActivityLogServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            DashboardStats stats = dashboardService.getAdminDashboardStats();
            List<ActivityLog> recentLogs = activityLogService.getRecentLogs(6);

            request.setAttribute("stats", stats);
            request.setAttribute("recentLogs", recentLogs);
            request.setAttribute("pageTitle", "Administration Dashboard - GardenConnect");
            request.setAttribute("activeTab", "dashboard");

            forward(request, response, "admin/dashboard");
        } catch (Exception e) {
            logger.error("Error loading admin dashboard: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load administrator overview.");
        }
    }
}
