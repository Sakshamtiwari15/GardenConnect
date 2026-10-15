package com.gardenconnect.controller.admin;

import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.Role;
import com.gardenconnect.model.User;
import com.gardenconnect.service.UserService;
import com.gardenconnect.service.impl.UserServiceImpl;
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
 * Controller for administrative user management: listing, role changes, account activation, and deletion.
 */
@WebServlet(name = "AdminUserController", urlPatterns = {
        "/admin/users",
        "/admin/users/create",
        "/admin/users/role",
        "/admin/users/status",
        "/admin/users/delete"
})
public class AdminUserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    public AdminUserController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
            int pageSize = ValidationUtil.parsePositiveInt(request.getParameter("size"), 10);

            String roleStr = request.getParameter("role");
            Role roleFilter = (roleStr != null && !roleStr.isEmpty()) ? Role.fromString(roleStr) : null;
            String searchQuery = request.getParameter("q");

            PaginationUtil<User> pageResult = userService.listUsers(page, pageSize, roleFilter, searchQuery);

            request.setAttribute("pageResult", pageResult);
            request.setAttribute("roles", Role.values());
            request.setAttribute("selectedRole", roleFilter);
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("pageTitle", "User Management - GardenConnect Admin");
            request.setAttribute("activeTab", "users");

            forward(request, response, "admin/users");
        } catch (Exception e) {
            logger.error("Error loading user management: {}", e.getMessage(), e);
            sendInternalError(request, response, "Unable to load user directory.");
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
            case "/admin/users/create":
                handleCreateUser(request, response, adminUser.getId());
                break;
            case "/admin/users/role":
                handleChangeRole(request, response, adminUser.getId());
                break;
            case "/admin/users/status":
                handleToggleStatus(request, response, adminUser.getId());
                break;
            case "/admin/users/delete":
                handleDeleteUser(request, response, adminUser.getId());
                break;
            default:
                sendNotFound(request, response, "Action not found");
        }
    }

    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response, Long adminId)
            throws IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String roleStr = request.getParameter("role");
        Role role = Role.fromString(roleStr);

        try {
            userService.createUserByAdmin(adminId, username, email, password, fullName, role);
            setFlashSuccess(request, "User '" + username + "' created successfully with role " + role.getDisplayName() + "!");
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }

    private void handleChangeRole(HttpServletRequest request, HttpServletResponse response, Long adminId)
            throws IOException {
        Long targetUserId = ValidationUtil.parsePositiveLong(request.getParameter("userId"), null);
        String roleStr = request.getParameter("role");
        Role newRole = Role.fromString(roleStr);

        if (targetUserId == null) {
            setFlashError(request, "Invalid user identifier.");
            redirect(request, response, "/admin/users");
            return;
        }

        try {
            userService.updateUserRole(adminId, targetUserId, newRole);
            setFlashSuccess(request, "User role successfully updated to " + newRole.getDisplayName() + ".");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response, Long adminId)
            throws IOException {
        Long targetUserId = ValidationUtil.parsePositiveLong(request.getParameter("userId"), null);
        boolean active = Boolean.parseBoolean(request.getParameter("active"));

        if (targetUserId == null) {
            setFlashError(request, "Invalid user identifier.");
            redirect(request, response, "/admin/users");
            return;
        }

        try {
            userService.toggleUserStatus(adminId, targetUserId, active);
            setFlashSuccess(request, "User status updated to " + (active ? "Active" : "Deactivated") + ".");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, Long adminId)
            throws IOException {
        Long targetUserId = ValidationUtil.parsePositiveLong(request.getParameter("userId"), null);

        if (targetUserId == null) {
            setFlashError(request, "Invalid user identifier.");
            redirect(request, response, "/admin/users");
            return;
        }

        try {
            userService.deleteUser(adminId, targetUserId);
            setFlashSuccess(request, "User account successfully removed.");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }
}
