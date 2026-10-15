package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.TipCategoryDAO;
import com.gardenconnect.dao.TipDAO;
import com.gardenconnect.dao.impl.TipCategoryDAOImpl;
import com.gardenconnect.dao.impl.TipDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.UnauthorizedActionException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.TipCategory;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.service.TipService;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for gardening tip publishing and lifecycle.
 */
public class TipServiceImpl implements TipService {

    private static final Logger logger = LoggerFactory.getLogger(TipServiceImpl.class);

    private final TipDAO tipDAO;
    private final TipCategoryDAO categoryDAO;
    private final AsyncActivityProcessor asyncActivityProcessor;

    public TipServiceImpl() {
        this(new TipDAOImpl(), new TipCategoryDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public TipServiceImpl(TipDAO tipDAO, TipCategoryDAO categoryDAO, AsyncActivityProcessor asyncActivityProcessor) {
        this.tipDAO = tipDAO;
        this.categoryDAO = categoryDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public Tip createTip(Long userId, String username, Integer categoryId, String title, String summary, String content, String imageUrl) throws GardenConnectException {
        validateTipInput(categoryId, title, content);

        Tip tip = new Tip();
        tip.setUserId(userId);
        tip.setCategoryId(categoryId);
        tip.setTitle(InputSanitizer.escapeHtml(title.trim()));
        tip.setSummary(summary != null ? InputSanitizer.escapeHtml(summary.trim()) : null);
        tip.setContent(InputSanitizer.stripMaliciousTags(content.trim()));
        tip.setImageUrl(imageUrl != null ? imageUrl.trim() : null);
        tip.setModerationStatus(ModerationStatus.PENDING); // Requires moderator approval
        tip.setViewCount(0);

        Tip created = tipDAO.create(tip);
        logger.info("New gardening tip created (ID: {}, User: {}) awaiting moderation.", created.getId(), userId);

        asyncActivityProcessor.submitLog(new ActivityLog(
                userId,
                username,
                "TIP_CREATED",
                "TIP",
                created.getId(),
                "Submitted tip: " + created.getTitle(),
                "127.0.0.1"
        ));

        return created;
    }

    @Override
    public boolean updateTip(Long tipId, Long userId, Integer categoryId, String title, String summary, String content, String imageUrl) throws GardenConnectException {
        Tip existing = tipDAO.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Tip", tipId));

        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only modify your own gardening tips.");
        }

        validateTipInput(categoryId, title, content);

        existing.setCategoryId(categoryId);
        existing.setTitle(InputSanitizer.escapeHtml(title.trim()));
        existing.setSummary(summary != null ? InputSanitizer.escapeHtml(summary.trim()) : null);
        existing.setContent(InputSanitizer.stripMaliciousTags(content.trim()));
        existing.setImageUrl(imageUrl != null ? imageUrl.trim() : null);
        // Resets moderation status to PENDING on edit for safety
        existing.setModerationStatus(ModerationStatus.PENDING);

        boolean updated = tipDAO.update(existing);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    existing.getAuthorUsername(),
                    "TIP_UPDATED",
                    "TIP",
                    tipId,
                    "Updated tip and reset status to PENDING: " + existing.getTitle(),
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean deleteTip(Long tipId, Long userId, boolean isAdmin) throws GardenConnectException {
        Tip existing = tipDAO.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Tip", tipId));

        if (!isAdmin && !existing.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You do not have permission to delete this gardening tip.");
        }

        boolean deleted = tipDAO.delete(tipId);
        if (deleted) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    isAdmin ? "admin" : existing.getAuthorUsername(),
                    "TIP_DELETED",
                    "TIP",
                    tipId,
                    "Deleted tip: " + existing.getTitle(),
                    "127.0.0.1"
            ));
        }
        return deleted;
    }

    @Override
    public Tip getTipDetails(Long tipId, boolean incrementViews) throws GardenConnectException {
        Tip tip = tipDAO.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Tip", tipId));

        if (incrementViews && tip.isApproved()) {
            tipDAO.incrementViewCount(tipId);
            tip.setViewCount(tip.getViewCount() + 1);
        }
        return tip;
    }

    @Override
    public PaginationUtil<Tip> browseApprovedTips(int page, int pageSize, Integer categoryId, String searchQuery, String sortBy) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Tip> items = tipDAO.findApproved(offset, pageSize, categoryId, searchQuery, sortBy);
        long total = tipDAO.countApproved(categoryId, searchQuery);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public PaginationUtil<Tip> getGardenerTips(Long userId, int page, int pageSize) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Tip> items = tipDAO.findByUserId(userId, offset, pageSize);
        long total = tipDAO.countByUserId(userId);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public List<TipCategory> getAllCategories() throws GardenConnectException {
        return categoryDAO.findAll();
    }

    @Override
    public List<Tip> getRecentFeaturedTips(int limit) throws GardenConnectException {
        return tipDAO.findRecentApproved(limit);
    }

    private void validateTipInput(Integer categoryId, String title, String content) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (categoryId == null || categoryId <= 0) {
            errors.put("categoryId", "Please select a valid tip category.");
        }
        if (!ValidationUtil.isWithinLength(title, 5, 200)) {
            errors.put("title", "Title must be between 5 and 200 characters.");
        }
        if (!ValidationUtil.isWithinLength(content, 20, 20000)) {
            errors.put("content", "Content must be at least 20 characters.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Tip validation failed", errors);
        }
    }
}
