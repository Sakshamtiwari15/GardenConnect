package com.gardenconnect.controller;

import com.gardenconnect.model.DashboardStats;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.TipCategory;
import com.gardenconnect.service.DashboardService;
import com.gardenconnect.service.DiscussionService;
import com.gardenconnect.service.TipService;
import com.gardenconnect.service.impl.DashboardServiceImpl;
import com.gardenconnect.service.impl.DiscussionServiceImpl;
import com.gardenconnect.service.impl.TipServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the public landing home page.
 * Loads live community stats, featured tips, categories, and recent discussions.
 */
@WebServlet(name = "HomeController", urlPatterns = {"", "/home"})
public class HomeController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final TipService tipService;
    private final DiscussionService discussionService;
    private final DashboardService dashboardService;

    public HomeController() {
        this.tipService = new TipServiceImpl();
        this.discussionService = new DiscussionServiceImpl();
        this.dashboardService = new DashboardServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            DashboardStats stats = dashboardService.getPublicCommunityStats();
            List<Tip> featuredTips = tipService.getRecentFeaturedTips(3);
            List<TipCategory> categories = tipService.getAllCategories();
            List<Discussion> recentDiscussions = discussionService.getRecentDiscussions(3);

            request.setAttribute("stats", stats);
            request.setAttribute("featuredTips", featuredTips);
            request.setAttribute("categories", categories);
            request.setAttribute("recentDiscussions", recentDiscussions);
            request.setAttribute("pageTitle", "GardenConnect - Grow, Share, and Flourish Together");

            forward(request, response, "home");
        } catch (Exception e) {
            logger.error("Error loading home page: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load community homepage.");
        }
    }
}
