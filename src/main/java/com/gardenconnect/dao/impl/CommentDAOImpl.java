package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.CommentDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CommentDAO.
 */
public class CommentDAOImpl implements CommentDAO {

    private static final Logger logger = LoggerFactory.getLogger(CommentDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT c.id, c.discussion_id, c.user_id, u.full_name AS author_name, u.username AS author_username, " +
            "       u.location AS author_location, c.content, c.created_at, c.updated_at " +
            "FROM comments c " +
            "JOIN users u ON c.user_id = u.id ";

    @Override
    public Optional<Comment> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToComment(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding comment by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find comment", e);
        }
    }

    @Override
    public List<Comment> findByDiscussionId(Long discussionId) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE c.discussion_id = ? ORDER BY c.created_at ASC";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discussionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving comments for discussion {}: {}", discussionId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve comments", e);
        }
    }

    @Override
    public Comment create(Comment comment) throws DatabaseOperationException {
        String sql = "INSERT INTO comments (discussion_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, comment.getDiscussionId());
            stmt.setLong(2, comment.getUserId());
            stmt.setString(3, comment.getContent());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseOperationException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getLong(1));
                }
            }
            return comment;
        } catch (SQLException e) {
            logger.error("Error creating comment: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save comment", e);
        }
    }

    @Override
    public boolean update(Comment comment) throws DatabaseOperationException {
        String sql = "UPDATE comments SET content = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, comment.getContent());
            stmt.setLong(2, comment.getId());
            stmt.setLong(3, comment.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating comment {}: {}", comment.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update comment", e);
        }
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting comment {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete comment", e);
        }
    }

    @Override
    public List<Comment> findAll(int offset, int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "ORDER BY c.created_at DESC LIMIT ? OFFSET ?";
        List<Comment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToComment(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving all comments: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve comments", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM comments";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting comments: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count comments", e);
        }
    }

    @Override
    public long countByDiscussionId(Long discussionId) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM comments WHERE discussion_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discussionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting comments for discussion {}: {}", discussionId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count discussion comments", e);
        }
    }

    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setDiscussionId(rs.getLong("discussion_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setAuthorName(rs.getString("author_name"));
        comment.setAuthorUsername(rs.getString("author_username"));
        comment.setAuthorLocation(rs.getString("author_location"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        comment.setUpdatedAt(rs.getTimestamp("updated_at"));
        return comment;
    }
}
