package com.gardenconnect.controller.gardener;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.DiscussionCategory;
import com.gardenconnect.model.User;
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
 * Controller for gardener discussion threads and interactive comments.
 */
@WebServlet(name = "GardenerDiscussionController", urlPatterns = {
        "/gardener/discussions",
        "/gardener/discussions/create",
        "/gardener/comments/create",
        "/gardener/comments/delete"
})
public class GardenerDiscussionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GardenerDiscussionController.class);

    private final DiscussionService discussionService;

    public GardenerDiscussionController() {
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
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), AppConfig.DEFAULT_PAGE_SIZE);

            PaginationUtil<Discussion> pageResult = discussionService.getGardenerDiscussions(user.getId(), page, pageSize);
            List<DiscussionCategory> categories = discussionService.getAllCategories();

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("categories", categories);
            request.setAttribute("pageTitle", "My Discussion Topics - GardenConnect");
            request.setAttribute("activeTab", "discussions");

            forward(request, response, "gardener/discussions");
        } catch (Exception e) {
            logger.error("Error listing discussions for user {}: {}", user.getId(), e.getMessage(), e);
            sendInternalError(request, response, "Unable to load your discussions.");
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
            case "/gardener/discussions/create":
                handleCreateDiscussion(request, response, user);
                break;
            case "/gardener/comments/create":
                handleCreateComment(request, response, user);
                break;
            case "/gardener/comments/delete":
                handleDeleteComment(request, response, user);
                break;
            default:
                sendNotFound(request, response, "Page not found");
        }
    }

    private void handleCreateDiscussion(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        String catStr = request.getParameter("categoryId");
        Integer categoryId = (catStr != null && !catStr.isEmpty()) ? Integer.parseInt(catStr) : null;
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        try {
            Discussion created = discussionService.createDiscussion(user.getId(), user.getUsername(), categoryId, title, content);
            setFlashSuccess(request, "Discussion started successfully!");
            redirect(request, response, "/discussions/view?id=" + created.getId());
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/discussions");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/discussions");
        }
    }

    private void handleCreateComment(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        Long discussionId = ValidationUtil.parsePositiveLong(request.getParameter("discussionId"), null);
        String content = request.getParameter("content");

        if (discussionId == null) {
            setFlashError(request, "Invalid discussion topic.");
            redirect(request, response, "/discussions");
            return;
        }

        try {
            discussionService.addComment(discussionId, user.getId(), user.getUsername(), content);
            setFlashSuccess(request, "Your reply has been posted!");
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/discussions/view?id=" + discussionId);
    }

    private void handleDeleteComment(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        Long commentId = ValidationUtil.parsePositiveLong(request.getParameter("commentId"), null);
        Long discussionId = ValidationUtil.parsePositiveLong(request.getParameter("discussionId"), null);

        if (commentId == null || discussionId == null) {
            setFlashError(request, "Invalid comment reference.");
            redirect(request, response, "/discussions");
            return;
        }

        try {
            discussionService.deleteComment(commentId, user.getId(), user.isAdmin());
            setFlashSuccess(request, "Comment deleted.");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/discussions/view?id=" + discussionId);
    }
}
