package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.ActivityProcessor;
import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.DiscussionDAO;
import com.gardenconnect.dao.TipDAO;
import com.gardenconnect.dao.impl.DiscussionDAOImpl;
import com.gardenconnect.dao.impl.TipDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.service.ModerationService;
import com.gardenconnect.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service implementation for content moderation workflows.
 */
public class ModerationServiceImpl implements ModerationService {

    private static final Logger logger = LoggerFactory.getLogger(ModerationServiceImpl.class);

    private final TipDAO tipDAO;
    private final DiscussionDAO discussionDAO;
    private final ActivityProcessor asyncActivityProcessor;

    public ModerationServiceImpl() {
        this(new TipDAOImpl(), new DiscussionDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public ModerationServiceImpl(TipDAO tipDAO, DiscussionDAO discussionDAO, ActivityProcessor asyncActivityProcessor) {
        this.tipDAO = tipDAO;
        this.discussionDAO = discussionDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public PaginationUtil<Tip> getTipsForModeration(int page, int pageSize, ModerationStatus statusFilter, String searchQuery) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Tip> items = tipDAO.findAllForModeration(offset, pageSize, statusFilter, searchQuery);
        long total = tipDAO.countForModeration(statusFilter, searchQuery);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public boolean approveTip(Long adminId, String adminUsername, Long tipId) throws GardenConnectException {
        Tip tip = tipDAO.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Tip", tipId));

        boolean updated = tipDAO.updateModeration(tipId, ModerationStatus.APPROVED, null);
        if (updated) {
            logger.info("Admin {} approved tip ID: {}", adminUsername, tipId);
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    adminUsername,
                    "TIP_APPROVED",
                    "TIP",
                    tipId,
                    "Approved gardening tip: " + tip.getTitle(),
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean rejectTip(Long adminId, String adminUsername, Long tipId, String reason) throws GardenConnectException {
        Tip tip = tipDAO.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Gardening Tip", tipId));

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("reason", "A rejection reason must be provided.");
        }

        String sanitizedReason = InputSanitizer.escapeHtml(reason.trim());
        boolean updated = tipDAO.updateModeration(tipId, ModerationStatus.REJECTED, sanitizedReason);
        if (updated) {
            logger.info("Admin {} rejected tip ID: {}. Reason: {}", adminUsername, tipId, sanitizedReason);
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    adminUsername,
                    "TIP_REJECTED",
                    "TIP",
                    tipId,
                    "Rejected tip: " + tip.getTitle() + " | Reason: " + sanitizedReason,
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public PaginationUtil<Discussion> getDiscussionsForModeration(int page, int pageSize, ModerationStatus statusFilter) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Discussion> items = discussionDAO.findAllForModeration(offset, pageSize, statusFilter);
        long total = discussionDAO.countForModeration(statusFilter);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public boolean approveDiscussion(Long adminId, String adminUsername, Long discussionId) throws GardenConnectException {
        Discussion discussion = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        boolean updated = discussionDAO.updateModeration(discussionId, ModerationStatus.APPROVED, null);
        if (updated) {
            logger.info("Admin {} approved discussion ID: {}", adminUsername, discussionId);
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    adminUsername,
                    "DISCUSSION_APPROVED",
                    "DISCUSSION",
                    discussionId,
                    "Approved discussion: " + discussion.getTitle(),
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean rejectDiscussion(Long adminId, String adminUsername, Long discussionId, String reason) throws GardenConnectException {
        Discussion discussion = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("reason", "A rejection reason must be provided.");
        }

        String sanitizedReason = InputSanitizer.escapeHtml(reason.trim());
        boolean updated = discussionDAO.updateModeration(discussionId, ModerationStatus.REJECTED, sanitizedReason);
        if (updated) {
            logger.info("Admin {} rejected discussion ID: {}. Reason: {}", adminUsername, discussionId, sanitizedReason);
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    adminUsername,
                    "DISCUSSION_REJECTED",
                    "DISCUSSION",
                    discussionId,
                    "Rejected discussion: " + discussion.getTitle() + " | Reason: " + sanitizedReason,
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public long getPendingTipsCount() throws GardenConnectException {
        return tipDAO.countByStatus(ModerationStatus.PENDING);
    }

    @Override
    public long getPendingDiscussionsCount() throws GardenConnectException {
        return discussionDAO.countForModeration(ModerationStatus.PENDING);
    }
}
