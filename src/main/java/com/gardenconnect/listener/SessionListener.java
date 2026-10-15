package com.gardenconnect.listener;

import com.gardenconnect.security.CsrfTokenManager;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Session lifecycle listener tracking active session metrics.
 * Demonstrates thread-safe AtomicInteger.
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
    private static final AtomicInteger ACTIVE_SESSIONS = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.incrementAndGet();
        // Pre-initialize CSRF token upon session creation
        CsrfTokenManager.getOrCreateToken(se.getSession());
        logger.debug("Session created: {} (Active sessions: {})", se.getSession().getId(), count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = ACTIVE_SESSIONS.decrementAndGet();
        logger.debug("Session destroyed: {} (Active sessions: {})", se.getSession().getId(), count);
    }

    public static int getActiveSessionsCount() {
        return ACTIVE_SESSIONS.get();
    }
}
