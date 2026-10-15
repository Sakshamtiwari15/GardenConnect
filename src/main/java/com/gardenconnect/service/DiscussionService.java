package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.Comment;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.DiscussionCategory;
import com.gardenconnect.util.PaginationUtil;

import java.util.List;

/**
 * Service managing community discussion threads, moderation, and nested comments.
 */
public interface DiscussionService {

    Discussion createDiscussion(Long userId, String username, Integer categoryId, String title, String content) throws GardenConnectException;

    boolean updateDiscussion(Long discussionId, Long userId, Integer categoryId, String title, String content) throws GardenConnectException;

    boolean deleteDiscussion(Long discussionId, Long userId, boolean isAdmin) throws GardenConnectException;

    Discussion getDiscussionDetails(Long discussionId, boolean incrementViews) throws GardenConnectException;

    List<Comment> getDiscussionComments(Long discussionId) throws GardenConnectException;

    Comment addComment(Long discussionId, Long userId, String username, String content) throws GardenConnectException;

    boolean deleteComment(Long commentId, Long userId, boolean isAdmin) throws GardenConnectException;

    PaginationUtil<Discussion> browseApprovedDiscussions(int page, int pageSize, Integer categoryId, String searchQuery) throws GardenConnectException;

    PaginationUtil<Discussion> getGardenerDiscussions(Long userId, int page, int pageSize) throws GardenConnectException;

    List<DiscussionCategory> getAllCategories() throws GardenConnectException;

    List<Discussion> getRecentDiscussions(int limit) throws GardenConnectException;
}
