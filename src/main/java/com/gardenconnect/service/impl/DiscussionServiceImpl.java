package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.CommentDAO;
import com.gardenconnect.dao.DiscussionCategoryDAO;
import com.gardenconnect.dao.DiscussionDAO;
import com.gardenconnect.dao.impl.CommentDAOImpl;
import com.gardenconnect.dao.impl.DiscussionCategoryDAOImpl;
import com.gardenconnect.dao.impl.DiscussionDAOImpl;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.exception.UnauthorizedActionException;
import com.gardenconnect.exception.ValidationException;
import com.gardenconnect.model.*;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.service.DiscussionService;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for discussions and interactive comments.
 */
public class DiscussionServiceImpl implements DiscussionService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionServiceImpl.class);

    private final DiscussionDAO discussionDAO;
    private final CommentDAO commentDAO;
    private final DiscussionCategoryDAO categoryDAO;
    private final AsyncActivityProcessor asyncActivityProcessor;

    public DiscussionServiceImpl() {
        this(new DiscussionDAOImpl(), new CommentDAOImpl(), new DiscussionCategoryDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public DiscussionServiceImpl(DiscussionDAO discussionDAO, CommentDAO commentDAO,
                                 DiscussionCategoryDAO categoryDAO, AsyncActivityProcessor asyncActivityProcessor) {
        this.discussionDAO = discussionDAO;
        this.commentDAO = commentDAO;
        this.categoryDAO = categoryDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public Discussion createDiscussion(Long userId, String username, Integer categoryId, String title, String content) throws GardenConnectException {
        validateDiscussionInput(categoryId, title, content);

        Discussion discussion = new Discussion();
        discussion.setUserId(userId);
        discussion.setCategoryId(categoryId);
        discussion.setTitle(InputSanitizer.escapeHtml(title.trim()));
        discussion.setContent(InputSanitizer.stripMaliciousTags(content.trim()));
        discussion.setModerationStatus(ModerationStatus.APPROVED); // Default approved or policy
        discussion.setViewCount(0);

        Discussion created = discussionDAO.create(discussion);
        logger.info("New discussion thread created (ID: {}, User: {})", created.getId(), userId);

        asyncActivityProcessor.submitLog(new ActivityLog(
                userId,
                username,
                "DISCUSSION_CREATED",
                "DISCUSSION",
                created.getId(),
                "Started discussion: " + created.getTitle(),
                "127.0.0.1"
        ));

        return created;
    }

    @Override
    public boolean updateDiscussion(Long discussionId, Long userId, Integer categoryId, String title, String content) throws GardenConnectException {
        Discussion existing = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        if (!existing.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only edit discussions that you created.");
        }

        validateDiscussionInput(categoryId, title, content);

        existing.setCategoryId(categoryId);
        existing.setTitle(InputSanitizer.escapeHtml(title.trim()));
        existing.setContent(InputSanitizer.stripMaliciousTags(content.trim()));

        boolean updated = discussionDAO.update(existing);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    existing.getAuthorUsername(),
                    "DISCUSSION_UPDATED",
                    "DISCUSSION",
                    discussionId,
                    "Updated discussion: " + existing.getTitle(),
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean deleteDiscussion(Long discussionId, Long userId, boolean isAdmin) throws GardenConnectException {
        Discussion existing = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        if (!isAdmin && !existing.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You do not have permission to delete this discussion.");
        }

        boolean deleted = discussionDAO.delete(discussionId);
        if (deleted) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    isAdmin ? "admin" : existing.getAuthorUsername(),
                    "DISCUSSION_DELETED",
                    "DISCUSSION",
                    discussionId,
                    "Deleted discussion thread: " + existing.getTitle(),
                    "127.0.0.1"
            ));
        }
        return deleted;
    }

    @Override
    public Discussion getDiscussionDetails(Long discussionId, boolean incrementViews) throws GardenConnectException {
        Discussion discussion = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        if (incrementViews) {
            discussionDAO.incrementViewCount(discussionId);
            discussion.setViewCount(discussion.getViewCount() + 1);
        }
        return discussion;
    }

    @Override
    public List<Comment> getDiscussionComments(Long discussionId) throws GardenConnectException {
        return commentDAO.findByDiscussionId(discussionId);
    }

    @Override
    public Comment addComment(Long discussionId, Long userId, String username, String content) throws GardenConnectException {
        if (!ValidationUtil.isWithinLength(content, 2, 5000)) {
            throw new ValidationException("content", "Comment must be between 2 and 5000 characters.");
        }

        // Verify discussion exists
        Discussion discussion = discussionDAO.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", discussionId));

        Comment comment = new Comment();
        comment.setDiscussionId(discussionId);
        comment.setUserId(userId);
        comment.setContent(InputSanitizer.escapeHtml(content.trim()));

        Comment created = commentDAO.create(comment);
        asyncActivityProcessor.submitLog(new ActivityLog(
                userId,
                username,
                "COMMENT_ADDED",
                "COMMENT",
                created.getId(),
                "Added comment on discussion: " + discussion.getTitle(),
                "127.0.0.1"
        ));

        return created;
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId, boolean isAdmin) throws GardenConnectException {
        Comment existing = commentDAO.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!isAdmin && !existing.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You do not have permission to delete this comment.");
        }

        boolean deleted = commentDAO.delete(commentId);
        if (deleted) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    isAdmin ? "admin" : existing.getAuthorUsername(),
                    "COMMENT_DELETED",
                    "COMMENT",
                    commentId,
                    "Deleted comment on discussion ID " + existing.getDiscussionId(),
                    "127.0.0.1"
            ));
        }
        return deleted;
    }

    @Override
    public PaginationUtil<Discussion> browseApprovedDiscussions(int page, int pageSize, Integer categoryId, String searchQuery) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Discussion> items = discussionDAO.findApproved(offset, pageSize, categoryId, searchQuery);
        long total = discussionDAO.countApproved(categoryId, searchQuery);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public PaginationUtil<Discussion> getGardenerDiscussions(Long userId, int page, int pageSize) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<Discussion> items = discussionDAO.findByUserId(userId, offset, pageSize);
        long total = discussionDAO.countByUserId(userId);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public List<DiscussionCategory> getAllCategories() throws GardenConnectException {
        return categoryDAO.findAll();
    }

    @Override
    public List<Discussion> getRecentDiscussions(int limit) throws GardenConnectException {
        return discussionDAO.findRecent(limit);
    }

    private void validateDiscussionInput(Integer categoryId, String title, String content) throws ValidationException {
        Map<String, String> errors = new HashMap<>();
        if (categoryId == null || categoryId <= 0) {
            errors.put("categoryId", "Please select a discussion category.");
        }
        if (!ValidationUtil.isWithinLength(title, 5, 200)) {
            errors.put("title", "Discussion title must be between 5 and 200 characters.");
        }
        if (!ValidationUtil.isWithinLength(content, 10, 20000)) {
            errors.put("content", "Discussion content must be at least 10 characters.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Discussion validation failed", errors);
        }
    }
}
