package com.gardenconnect.controller.gardener;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.controller.BaseController;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.User;
import com.gardenconnect.service.UserService;
import com.gardenconnect.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for managing gardener personal profiles and secure password changes.
 */
@WebServlet(name = "GardenerProfileController", urlPatterns = {"/gardener/profile", "/gardener/profile/password"})
public class GardenerProfileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(GardenerProfileController.class);

    private final UserService userService;

    public GardenerProfileController() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User sessionUser = getAuthenticatedUser(request);
        if (sessionUser == null) {
            redirect(request, response, "/login");
            return;
        }

        try {
            // Reload latest user data from database
            User user = userService.getUserById(sessionUser.getId());
            request.setAttribute("user", user);
            request.setAttribute("pageTitle", "My Profile - GardenConnect");
            request.setAttribute("activeTab", "profile");
            forward(request, response, "gardener/profile");
        } catch (Exception e) {
            logger.error("Error retrieving profile for user {}: {}", sessionUser.getId(), e.getMessage(), e);
            sendInternalError(request, response, "Unable to load profile.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        User sessionUser = getAuthenticatedUser(request);
        if (sessionUser == null) {
            redirect(request, response, "/login");
            return;
        }

        if ("/gardener/profile/password".equals(path)) {
            handlePasswordChange(request, response, sessionUser.getId());
        } else {
            handleProfileUpdate(request, response, sessionUser);
        }
    }

    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response, User sessionUser)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String bio = request.getParameter("bio");
        String location = request.getParameter("location");
        String gardeningInterests = request.getParameter("gardeningInterests");

        try {
            userService.updateProfile(sessionUser.getId(), fullName, bio, location, gardeningInterests);

            // Refresh user in session
            User updatedUser = userService.getUserById(sessionUser.getId());
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute(AppConfig.SESSION_USER, updatedUser);
            }

            setFlashSuccess(request, "Your gardening profile has been updated successfully!");
            redirect(request, response, "/gardener/profile");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute(AppConfig.REQ_ERROR_MAP, e.getFieldErrors());
            request.setAttribute("user", sessionUser);
            request.setAttribute("pageTitle", "My Profile - GardenConnect");
            forward(request, response, "gardener/profile");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/profile");
        }
    }

    private void handlePasswordChange(HttpServletRequest request, HttpServletResponse response, Long userId)
            throws ServletException, IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            userService.changePassword(userId, currentPassword, newPassword, confirmPassword);
            setFlashSuccess(request, "Your password has been changed securely.");
            redirect(request, response, "/gardener/profile");
        } catch (ValidationException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/profile");
        } catch (GardenConnectException e) {
            setFlashError(request, e.getMessage());
            redirect(request, response, "/gardener/profile");
        }
    }
}
