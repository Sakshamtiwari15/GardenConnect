package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Comment;

import java.util.List;

/**
 * Data Access Object for discussion thread comments.
 */
public interface CommentDAO extends BaseDAO<Comment, Long> {

    List<Comment> findByDiscussionId(Long discussionId) throws DatabaseOperationException;

    long countByDiscussionId(Long discussionId) throws DatabaseOperationException;
}
