package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;

import java.util.List;
import java.util.Map;

/**
 * Data Access Object for gardening tips.
 */
public interface TipDAO extends BaseDAO<Tip, Long> {

    boolean updateModeration(Long tipId, ModerationStatus status, String rejectionReason) throws DatabaseOperationException;

    boolean incrementViewCount(Long tipId) throws DatabaseOperationException;

    List<Tip> findApproved(int offset, int limit, Integer categoryId, String searchQuery, String sortBy) throws DatabaseOperationException;

    long countApproved(Integer categoryId, String searchQuery) throws DatabaseOperationException;

    List<Tip> findByUserId(Long userId, int offset, int limit) throws DatabaseOperationException;

    long countByUserId(Long userId) throws DatabaseOperationException;

    List<Tip> findAllForModeration(int offset, int limit, ModerationStatus statusFilter, String searchQuery) throws DatabaseOperationException;

    long countForModeration(ModerationStatus statusFilter, String searchQuery) throws DatabaseOperationException;

    List<Tip> findRecentApproved(int limit) throws DatabaseOperationException;

    long countByStatus(ModerationStatus status) throws DatabaseOperationException;

    Map<String, Long> getTipCountByCategory() throws DatabaseOperationException;
}
