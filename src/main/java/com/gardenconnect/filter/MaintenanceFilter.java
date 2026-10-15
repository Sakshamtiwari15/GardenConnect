package com.gardenconnect.filter;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.model.User;
import com.gardenconnect.service.SystemSettingService;
import com.gardenconnect.service.impl.SystemSettingServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Intercepts requests when Maintenance Mode is enabled.
 * Allows administrators and static assets to bypass maintenance restriction.
 */
@WebFilter(filterName = "MaintenanceFilter", urlPatterns = "/*")
public class MaintenanceFilter implements Filter {

    private SystemSettingService systemSettingService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.systemSettingService = new SystemSettingServiceImpl();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Always permit static assets, login, and admin portal routes
        if (path.startsWith("/assets/") || path.startsWith("/login") || path.startsWith("/logout") || path.startsWith("/admin")) {
            chain.doFilter(req, res);
            return;
        }

        if (systemSettingService.isMaintenanceMode()) {
            HttpSession session = request.getSession(false);
            User currentUser = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

            // Allow administrators to access the site during maintenance
            if (currentUser != null && currentUser.isAdmin()) {
                chain.doFilter(req, res);
                return;
            }

            request.getRequestDispatcher("/WEB-INF/views/errors/maintenance.jsp").forward(request, response);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
