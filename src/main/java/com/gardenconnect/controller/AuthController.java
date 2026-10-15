package com.gardenconnect.controller;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.User;
import com.gardenconnect.security.CsrfTokenManager;
import com.gardenconnect.service.SystemSettingService;
import com.gardenconnect.service.UserService;
import com.gardenconnect.service.impl.SystemSettingServiceImpl;
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
 * Controller handling user authentication lifecycle: Login, Registration, and Logout.
 * Implements session fixation protection and secure cookie handling.
 */
@WebServlet(name = "AuthController", urlPatterns = {"/login", "/register", "/logout"})
public class AuthController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final SystemSettingService systemSettingService;

    public AuthController() {
        this.userService = new UserServiceImpl();
        this.systemSettingService = new SystemSettingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Redirect already logged-in users away from auth pages
        User currentUser = getAuthenticatedUser(request);
        if (currentUser != null && !"/logout".equals(path)) {
            if (currentUser.isAdmin()) {
                redirect(request, response, "/admin/dashboard");
            } else {
                redirect(request, response, "/gardener/dashboard");
            }
            return;
        }

        switch (path) {
            case "/login":
                handleGetLogin(request, response);
                break;
            case "/register":
                handleGetRegister(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                sendNotFound(request, response, "Page not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/login":
                handlePostLogin(request, response);
                break;
            case "/register":
                handlePostRegister(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                sendNotFound(request, response, "Page not found");
        }
    }

    private void handleGetLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("returnUrl", request.getParameter("returnUrl"));
        request.setAttribute("pageTitle", "Login - GardenConnect");
        forward(request, response, "auth/login");
    }

    private void handleGetRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!systemSettingService.isRegistrationOpen()) {
            setFlashWarning(request, "New gardener registration is temporarily paused by platform administrators.");
            redirect(request, response, "/login");
            return;
        }
        request.setAttribute("pageTitle", "Join GardenConnect - Create Your Account");
        forward(request, response, "auth/register");
    }

    private void handlePostLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");
        String clientIp = extractClientIp(request);

        try {
            User user = userService.login(usernameOrEmail, password, clientIp);

            // Session fixation protection: invalidate old session and start fresh
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute(AppConfig.SESSION_USER, user);
            // Refresh CSRF token for the new authenticated session
            CsrfTokenManager.getOrCreateToken(newSession);

            setFlashSuccess(request, "Welcome back, " + user.getFullName() + "!");

            if (returnUrl != null && !returnUrl.trim().isEmpty() && returnUrl.startsWith(request.getContextPath())) {
                response.sendRedirect(returnUrl.trim());
            } else if (user.isAdmin()) {
                redirect(request, response, "/admin/dashboard");
            } else {
                redirect(request, response, "/gardener/dashboard");
            }
        } catch (GardenConnectException e) {
            logger.warn("Login failure for '{}': {}", usernameOrEmail, e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("usernameOrEmail", usernameOrEmail);
            request.setAttribute("returnUrl", returnUrl);
            request.setAttribute("pageTitle", "Login - GardenConnect");
            forward(request, response, "auth/login");
        }
    }

    private void handlePostRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!systemSettingService.isRegistrationOpen()) {
            setFlashWarning(request, "Registration is currently paused.");
            redirect(request, response, "/login");
            return;
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password != null && !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Password and confirmation do not match.");
            populateRegisterFormFields(request, username, email, fullName);
            forward(request, response, "auth/register");
            return;
        }

        try {
            userService.register(username, email, password, fullName);
            setFlashSuccess(request, "Account created successfully! Please sign in with your credentials.");
            redirect(request, response, "/login");
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute(AppConfig.REQ_ERROR_MAP, e.getFieldErrors());
            populateRegisterFormFields(request, username, email, fullName);
            forward(request, response, "auth/register");
        } catch (GardenConnectException e) {
            request.setAttribute("errorMessage", e.getMessage());
            populateRegisterFormFields(request, username, email, fullName);
            forward(request, response, "auth/register");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        setFlashSuccess(request, "You have been safely signed out. Happy gardening!");
        redirect(request, response, "/login");
    }

    private void populateRegisterFormFields(HttpServletRequest request, String username, String email, String fullName) {
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("fullName", fullName);
        request.setAttribute("pageTitle", "Join GardenConnect - Create Your Account");
    }
}
