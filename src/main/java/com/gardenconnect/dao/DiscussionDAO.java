package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.ModerationStatus;

import java.util.List;

/**
 * Data Access Object for community discussions.
 */
public interface DiscussionDAO extends BaseDAO<Discussion, Long> {

    boolean updateModeration(Long discussionId, ModerationStatus status, String rejectionReason) throws DatabaseOperationException;

    boolean incrementViewCount(Long id) throws DatabaseOperationException;

    List<Discussion> findApproved(int offset, int limit, Integer categoryId, String searchQuery) throws DatabaseOperationException;

    long countApproved(Integer categoryId, String searchQuery) throws DatabaseOperationException;

    List<Discussion> findByUserId(Long userId, int offset, int limit) throws DatabaseOperationException;

    long countByUserId(Long userId) throws DatabaseOperationException;

    List<Discussion> findAllForModeration(int offset, int limit, ModerationStatus status) throws DatabaseOperationException;

    long countForModeration(ModerationStatus status) throws DatabaseOperationException;

    List<Discussion> findRecent(int limit) throws DatabaseOperationException;
}
