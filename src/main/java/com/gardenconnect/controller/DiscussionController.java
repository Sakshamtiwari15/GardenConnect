package com.gardenconnect.controller;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.model.Comment;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.DiscussionCategory;
import com.gardenconnect.service.DiscussionService;
import com.gardenconnect.service.impl.DiscussionServiceImpl;
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
 * Public controller for browsing community discussions and reading comment threads.
 */
@WebServlet(name = "DiscussionController", urlPatterns = {"/discussions", "/discussions/view"})
public class DiscussionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionController.class);

    private final DiscussionService discussionService;

    public DiscussionController() {
        this.discussionService = new DiscussionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/discussions/view".equals(path)) {
            handleViewDiscussion(request, response);
        } else {
            handleListDiscussions(request, response);
        }
    }

    private void handleListDiscussions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), AppConfig.DEFAULT_PAGE_SIZE);

            String catParam = request.getParameter("category");
            Integer categoryId = (catParam != null && !catParam.isEmpty()) ? Integer.parseInt(catParam) : null;
            String query = request.getParameter("q");

            PaginationUtil<Discussion> pageResult = discussionService.browseApprovedDiscussions(page, pageSize, categoryId, query);
            List<DiscussionCategory> categories = discussionService.getAllCategories();

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("categories", categories);
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("searchQuery", query);
            request.setAttribute("pageTitle", "Community Discussions - GardenConnect");

            forward(request, response, "discussions/list");
        } catch (Exception e) {
            logger.error("Error listing discussions: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load community discussions.");
        }
    }

    private void handleViewDiscussion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        Long id = ValidationUtil.parsePositiveLong(idParam, null);

        if (id == null) {
            sendNotFound(request, response, "Invalid discussion identifier specified.");
            return;
        }

        try {
            Discussion discussion = discussionService.getDiscussionDetails(id, true);
            List<Comment> comments = discussionService.getDiscussionComments(id);
            List<DiscussionCategory> categories = discussionService.getAllCategories();

            request.setAttribute("discussion", discussion);
            request.setAttribute("comments", comments);
            request.setAttribute("categories", categories);
            request.setAttribute("pageTitle", discussion.getTitle() + " - GardenConnect");

            forward(request, response, "discussions/detail");
        } catch (ResourceNotFoundException e) {
            sendNotFound(request, response, e.getMessage());
        } catch (GardenConnectException e) {
            logger.error("Error loading discussion {}: {}", id, e.getMessage(), e);
            sendInternalError(request, response, "Unable to load the requested discussion.");
        }
    }
}
