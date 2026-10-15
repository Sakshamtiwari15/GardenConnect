package com.gardenconnect.filter;

import com.gardenconnect.security.CsrfTokenManager;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter that enforces Cross-Site Request Forgery (CSRF) token validation
 * for state-changing HTTP methods (POST, PUT, DELETE, PATCH).
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Ensure session always has a CSRF token initialized
        HttpSession session = request.getSession(true);
        String token = CsrfTokenManager.getOrCreateToken(session);
        request.setAttribute("csrfToken", token);

        String method = request.getMethod().toUpperCase();
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Skip validation for safe read-only methods and static assets
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method) || "TRACE".equals(method)
                || path.startsWith("/assets/")) {
            chain.doFilter(req, res);
            return;
        }

        // Validate state-changing operations
        if (!CsrfTokenManager.isValidToken(request)) {
            logger.warn("CSRF token validation failed for path: {} [method: {}]", path, method);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("errorMessage", "Security verification failed (Invalid or missing CSRF token). Please try submitting the form again.");
            request.getRequestDispatcher("/WEB-INF/views/errors/403.jsp").forward(request, response);
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
