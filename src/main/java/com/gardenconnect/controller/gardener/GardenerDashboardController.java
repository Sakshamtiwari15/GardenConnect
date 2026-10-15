package com.gardenconnect.controller.gardener;

import com.gardenconnect.controller.BaseController;
import com.gardenconnect.model.DashboardStats;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.User;
import com.gardenconnect.service.DashboardService;
import com.gardenconnect.service.DiscussionService;
import com.gardenconnect.service.ProjectService;
import com.gardenconnect.service.TipService;
import com.gardenconnect.service.impl.DashboardServiceImpl;
import com.gardenconnect.service.impl.DiscussionServiceImpl;
import com.gardenconnect.service.impl.ProjectServiceImpl;
import com.gardenconnect.service.impl.TipServiceImpl;
import com.gardenconnect.util.PaginationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the personalized Gardener dashboard.
 * Aggregates user metrics, active projects progress, recent tips, and community discussions.
 */
@WebServlet(name = "GardenerDashboardController", urlPatterns = "/gardener/dashboard")
public class GardenerDashboardController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GardenerDashboardController.class);

    private final DashboardService dashboardService;
    private final TipService tipService;
    private final ProjectService projectService;
    private final DiscussionService discussionService;

    public GardenerDashboardController() {
        this.dashboardService = new DashboardServiceImpl();
        this.tipService = new TipServiceImpl();
        this.projectService = new ProjectServiceImpl();
        this.discussionService = new DiscussionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(request);
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }

        try {
            Long userId = user.getId();
            DashboardStats stats = dashboardService.getGardenerDashboardStats(userId);
            List<GardeningProject> recentProjects = projectService.getRecentProjects(userId, 4);
            PaginationUtil<Tip> userTips = tipService.getGardenerTips(userId, 1, 4);
            List<Discussion> recentDiscussions = discussionService.getRecentDiscussions(4);

            request.setAttribute("stats", stats);
            request.setAttribute("recentProjects", recentProjects);
            request.setAttribute("userTips", userTips.getItems());
            request.setAttribute("recentDiscussions", recentDiscussions);
            request.setAttribute("pageTitle", "Gardener Dashboard - GardenConnect");
            request.setAttribute("activeTab", "dashboard");

            forward(request, response, "gardener/dashboard");
        } catch (Exception e) {
            logger.error("Error loading gardener dashboard for user {}: {}", user.getId(), e.getMessage(), e);
            sendInternalError(request, response, "Unable to load gardener dashboard.");
        }
    }
}
