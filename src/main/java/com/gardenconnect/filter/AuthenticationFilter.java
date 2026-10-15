package com.gardenconnect.filter;

import com.gardenconnect.config.AppConfig;
import com.gardenconnect.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Ensures requests to protected member zones (/gardener/* and /admin/*)
 * have an active authenticated user session.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/gardener/*", "/admin/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(AppConfig.SESSION_USER) : null;

        if (currentUser == null) {
            String originalUri = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null && !query.isEmpty()) {
                originalUri += "?" + query;
            }

            String encodedReturnUrl = URLEncoder.encode(originalUri, StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/login?returnUrl=" + encodedReturnUrl);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
