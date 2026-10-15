package com.gardenconnect.controller.admin;

import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.User;
import com.gardenconnect.service.ModerationService;
import com.gardenconnect.service.impl.ModerationServiceImpl;
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
 * Controller for the Content Moderation Queue.
 * Allows administrators to approve or reject community tips and discussions.
 */
@WebServlet(name = "AdminModerationController", urlPatterns = {
        "/admin/moderation",
        "/admin/moderation/approve",
        "/admin/moderation/reject"
})
public class AdminModerationController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminModerationController.class);

    private final ModerationService moderationService;

    public AdminModerationController() {
        this.moderationService = new ModerationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), 10);

            String statusStr = request.getParameter("status");
            ModerationStatus statusFilter = (statusStr != null && !statusStr.isEmpty())
                    ? ModerationStatus.fromString(statusStr)
                    : ModerationStatus.PENDING;

            String searchQuery = request.getParameter("q");

            PaginationUtil<Tip> tipsPage = moderationService.getTipsForModeration(page, pageSize, statusFilter, searchQuery);
            long pendingTipsCount = moderationService.getPendingTipsCount();
            long pendingDiscCount = moderationService.getPendingDiscussionsCount();

            request.setAttribute("tipsPage", tipsPage);
            request.setAttribute("selectedStatus", statusFilter);
            request.setAttribute("statuses", ModerationStatus.values());
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("pendingTipsCount", pendingTipsCount);
            request.setAttribute("pendingDiscCount", pendingDiscCount);
            request.setAttribute("pageTitle", "Content Moderation Queue - GardenConnect Admin");
            request.setAttribute("activeTab", "moderation");

            forward(request, response, "admin/moderation");
        } catch (Exception e) {
            logger.error("Error loading moderation queue: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load moderation queue.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        User adminUser = getAuthenticatedUser(request);
        if (adminUser == null || !adminUser.isAdmin()) {
            sendForbidden(request, response, "Admin authentication required.");
            return;
        }

        switch (path) {
            case "/admin/moderation/approve":
                handleApprove(request, response, adminUser);
                break;
            case "/admin/moderation/reject":
                handleReject(request, response, adminUser);
                break;
            default:
                sendNotFound(request, response, "Action not found");
        }
    }

    private void handleApprove(HttpServletRequest request, HttpServletResponse response, User admin)
            throws IOException {
        String type = request.getParameter("type");
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);

        if (id == null) {
            setFlashError(request, "Invalid content ID.");
            redirect(request, response, "/admin/moderation");
            return;
        }

        try {
            if ("discussion".equalsIgnoreCase(type)) {
                moderationService.approveDiscussion(admin.getId(), admin.getUsername(), id);
                setFlashSuccess(request, "Discussion approved and published to the community feed.");
            } else {
                moderationService.approveTip(admin.getId(), admin.getUsername(), id);
                setFlashSuccess(request, "Gardening tip approved and published to the community feed.");
            }
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/moderation");
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response, User admin)
            throws IOException {
        String type = request.getParameter("type");
        Long id = ValidationUtil.parsePositiveLong(request.getParameter("id"), null);
        String reason = request.getParameter("reason");

        if (id == null) {
            setFlashError(request, "Invalid content ID.");
            redirect(request, response, "/admin/moderation");
            return;
        }

        try {
            if ("discussion".equalsIgnoreCase(type)) {
                moderationService.rejectDiscussion(admin.getId(), admin.getUsername(), id, reason);
                setFlashSuccess(request, "Discussion rejected with reason recorded.");
            } else {
                moderationService.rejectTip(admin.getId(), admin.getUsername(), id, reason);
                setFlashSuccess(request, "Gardening tip rejected with reason recorded.");
            }
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/moderation");
    }
}
