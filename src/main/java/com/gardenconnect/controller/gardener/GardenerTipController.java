package com.gardenconnect.controller.gardener;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.TipCategory;
import com.gardenconnect.model.User;
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
 * Controller for gardener tip authoring, editing, moderation status tracking, and deletion.
 */
@WebServlet(name = "GardenerTipController", urlPatterns = {
        "/gardener/tips",
        "/gardener/tips/create",
        "/gardener/tips/edit",
        "/gardener/tips/delete"
})
public class GardenerTipController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GardenerTipController.class);

    private final TipService tipService;

    public GardenerTipController() {
        this.tipService = new TipServiceImpl();
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
            case "/gardener/tips/create":
                showCreateForm(request, response);
                break;
            case "/gardener/tips/edit":
                showEditForm(request, response, user.getId());
                break;
            case "/gardener/tips":
            default:
                listGardenerTips(request, response, user.getId());
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
            case "/gardener/tips/create":
                handleCreateTip(request, response, user);
                break;
            case "/gardener/tips/edit":
                handleUpdateTip(request, response, user);
                break;
            case "/gardener/tips/delete":
                handleDeleteTip(request, response, user);
                break;
            default:
                sendNotFound(request, response, "Page not found");
        }
    }

    private void listGardenerTips(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), AppConfig.DEFAULT_PAGE_SIZE);

            PaginationUtil<Tip> pageResult = tipService.getGardenerTips(userId, page, pageSize);

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("pageTitle", "My Gardening Tips - GardenConnect");
            request.setAttribute("activeTab", "tips");

            forward(request, response, "gardener/tips");
        } catch (Exception e) {
            logger.error("Error listing tips for user {}: {}", userId, e.getMessage(), e);
            sendInternalError(request, response, "Unable to load your tips.");
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<TipCategory> categories = tipService.getAllCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("pageTitle", "Share a Gardening Tip - GardenConnect");
            request.setAttribute("activeTab", "tips");
            forward(request, response, "gardener/tip-form");
        } catch (Exception e) {
            sendInternalError(request, response, "Unable to open tip creation form.");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            sendNotFound(request, response, "Invalid tip ID specified.");
            return;
        }

        try {
            Tip tip = tipService.getTipDetails(id, false);
            if (!tip.getUserId().equals(userId)) {
                sendForbidden(request, response, "You can only edit tips that belong to you.");
                return;
            }

            List<TipCategory> categories = tipService.getAllCategories();
            request.setAttribute("tip", tip);
            request.setAttribute("categories", categories);
            request.setAttribute("pageTitle", "Edit Gardening Tip - GardenConnect");
            request.setAttribute("activeTab", "tips");
            forward(request, response, "gardener/tip-form");
        } catch (Exception e) {
            sendInternalError(request, response, "Unable to load tip for editing.");
        }
    }

    private void handleCreateTip(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        String catStr = request.getParameter("categoryId");
        Integer categoryId = (catStr != null && !catStr.isEmpty()) ? Integer.parseInt(catStr) : null;
        String title = request.getParameter("title");
        String summary = request.getParameter("summary");
        String content = request.getParameter("content");
        String imageUrl = request.getParameter("imageUrl");

        try {
            tipService.createTip(user.getId(), user.getUsername(), categoryId, title, summary, content, imageUrl);
            setFlashSuccess(request, "Your gardening tip has been submitted! It will appear publicly as soon as a moderator reviews it.");
            redirect(request, response, "/gardener/tips");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute(AppConfig.REQ_ERROR_MAP, e.getFieldErrors());
            request.setAttribute("title", title);
            request.setAttribute("summary", summary);
            request.setAttribute("content", content);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("selectedCategoryId", categoryId);
            showCreateForm(request, response);
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/tips");
        }
    }

    private void handleUpdateTip(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            sendNotFound(request, response, "Invalid tip ID specified.");
            return;
        }

        String catStr = request.getParameter("categoryId");
        Integer categoryId = (catStr != null && !catStr.isEmpty()) ? Integer.parseInt(catStr) : null;
        String title = request.getParameter("title");
        String summary = request.getParameter("summary");
        String content = request.getParameter("content");
        String imageUrl = request.getParameter("imageUrl");

        try {
            tipService.updateTip(id, user.getId(), categoryId, title, summary, content, imageUrl);
            setFlashSuccess(request, "Tip updated successfully! It has been submitted for moderation review.");
            redirect(request, response, "/gardener/tips");
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/tips/edit?id=" + id);
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/tips");
        }
    }

    private void handleDeleteTip(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        if (id == null) {
            setFlashError(request, "Invalid tip ID.");
            redirect(request, response, "/gardener/tips");
            return;
        }

        try {
            tipService.deleteTip(id, user.getId(), user.isAdmin());
            setFlashSuccess(request, "Gardening tip was deleted successfully.");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/gardener/tips");
    }
}
