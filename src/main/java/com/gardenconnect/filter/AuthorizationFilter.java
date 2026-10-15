package com.gardenconnect.filter;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Role-Based Access Control (RBAC) filter restricting /admin/* paths strictly to users with Role.ADMIN.
 */
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = "/admin/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("errorMessage", "Access Denied: You do not possess administrator credentials to view this area.");
            request.getRequestDispatcher("/WEB-INF/views/errors/403.jsp").forward(request, response);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
