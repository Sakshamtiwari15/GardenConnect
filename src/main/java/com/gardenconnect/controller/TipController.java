package com.gardenconnect.controller;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.TipCategory;
import com.gardenconnect.service.TipService;
import com.gardenconnect.service.impl.TipServiceImpl;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Public controller for browsing, searching, filtering, and viewing gardening tips.
 */
@WebServlet(name = "TipController", urlPatterns = {"/tips", "/tips/view"})
public class TipController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TipController.class);

    private final TipService tipService;

    public TipController() {
        this.tipService = new TipServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/tips/view".equals(path)) {
            handleViewTip(request, response);
        } else {
            handleListTips(request, response);
        }
    }

    private void handleListTips(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), AppConfig.DEFAULT_PAGE_SIZE);

            String catParam = request.getParameter("category");
            Integer categoryId = (catParam != null && !catParam.isEmpty()) ? Integer.parseInt(catParam) : null;
            String query = request.getParameter("q");
            String sort = request.getParameter("sort");
            if (sort == null || sort.trim().isEmpty()) {
                sort = "newest";
            }

            PaginationUtil<Tip> pageResult = tipService.browseApprovedTips(page, pageSize, categoryId, query, sort);
            List<TipCategory> categories = tipService.getAllCategories();

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("categories", categories);
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("searchQuery", query);
            request.setAttribute("currentSort", sort);
            request.setAttribute("pageTitle", "Gardening Tips & Techniques - GardenConnect");

            forward(request, response, "tips/list");
        } catch (Exception e) {
            logger.error("Error listing tips: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load gardening tips feed.");
        }
    }

    private void handleViewTip(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Long id = ValidationUtil.parsePositiveLong(idParam, null);

        if (id == null) {
            sendNotFound(request, response, "Invalid tip identifier specified.");
            return;
        }

        try {
            Tip tip = tipService.getTipDetails(id, true);

            // Allow only approved tips to be viewed by the public, unless the viewer is author or admin
            if (!tip.isApproved()) {
                Long currentUserId = getAuthenticatedUserId(request);
                boolean isAuthor = (currentUserId != null && currentUserId.equals(tip.getUserId()));
                boolean isAdmin = (getAuthenticatedUser(request) != null && getAuthenticatedUser(request).isAdmin());

                if (!isAuthor && !isAdmin) {
                    sendNotFound(request, response, "This gardening tip is currently under moderation review.");
                    return;
                }
            }

            List<Tip> recentTips = tipService.getRecentFeaturedTips(3);

            request.setAttribute("tip", tip);
            request.setAttribute("recentTips", recentTips);
            request.setAttribute("pageTitle", tip.getTitle() + " - GardenConnect");

            forward(request, response, "tips/detail");
        } catch (ResourceNotFoundException e) {
            sendNotFound(request, response, e.getMessage());
        } catch (GardenConnectException e) {
            logger.error("Error loading tip {}: {}", id, e.getMessage(), e);
            sendInternalError(request, response, "Unable to load the requested tip.");
        }
    }
}
