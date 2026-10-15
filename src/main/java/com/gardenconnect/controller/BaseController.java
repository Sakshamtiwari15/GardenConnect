package com.gardenconnect.controller;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Base abstract controller class providing standard MVC forwarding, redirecting,
 * session retrieval, and flash messaging utilities.
 * Demonstrates OOP Inheritance and Abstraction.
 */
public abstract class BaseController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Forwards request and response to a JSP view inside /WEB-INF/views/.
     */
    protected void forward(HttpServletRequest request, HttpServletResponse response, String viewName)
            throws ServletException, IOException {
        String viewPath = "/WEB-INF/views/" + viewName + ".jsp";
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    /**
     * Sends an HTTP redirect appending application context path.
     */
    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        String target = path.startsWith("/") ? request.getContextPath() + path : path;
        response.sendRedirect(target);
    }

    /**
     * Retrieves the currently logged-in user from the session, or null if unauthenticated.
     */
    protected User getAuthenticatedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(AppConfig.SESSION_USER);
        }
        return null;
    }

    /**
     * Retrieves the authenticated user's ID or null.
     */
    protected Long getAuthenticatedUserId(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return user != null ? user.getId() : null;
    }

    /**
     * Sets a temporary success flash message in session.
     */
    protected void setFlashSuccess(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.FLASH_SUCCESS, message);
    }

    /**
     * Sets a temporary error flash message in session.
     */
    protected void setFlashError(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.FLASH_ERROR, message);
    }

    /**
     * Sets a temporary warning flash message in session.
     */
    protected void setFlashWarning(HttpServletRequest request, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute(AppConfig.FLASH_WARNING, message);
    }

    /**
     * Resolves the real IP address of the client (considering proxy headers).
     */
    protected String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return (ip != null && !ip.isEmpty()) ? ip : "127.0.0.1";
    }

    protected void sendNotFound(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("errorMessage", message != null ? message : "The requested resource was not found.");
        forward(request, response, "errors/404");
    }

    protected void sendForbidden(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.setAttribute("errorMessage", message != null ? message : "You do not have permission to perform this action.");
        forward(request, response, "errors/403");
    }

    protected void sendInternalError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("errorMessage", message != null ? message : "An unexpected server error occurred.");
        forward(request, response, "errors/500");
    }
}
