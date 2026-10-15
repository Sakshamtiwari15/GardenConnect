package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.DiscussionDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.ModerationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of DiscussionDAO with comment count subqueries and author joins.
 */
public class DiscussionDAOImpl implements DiscussionDAO {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT d.id, d.user_id, u.full_name AS author_name, u.username AS author_username, " +
            "       d.category_id, c.name AS category_name, c.slug AS category_slug, " +
            "       d.title, d.content, d.moderation_status, d.rejection_reason, d.view_count, " +
            "       (SELECT COUNT(*) FROM comments cm WHERE cm.discussion_id = d.id) AS comment_count, " +
            "       d.created_at, d.updated_at " +
            "FROM discussions d " +
            "JOIN users u ON d.user_id = u.id " +
            "JOIN discussion_categories c ON d.category_id = c.id ";

    @Override
    public Optional<Discussion> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE d.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDiscussion(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding discussion by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find discussion by id", e);
        }
    }

    @Override
    public Discussion create(Discussion discussion) throws DatabaseOperationException {
        String sql = "INSERT INTO discussions (user_id, category_id, title, content, moderation_status, view_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, discussion.getUserId());
            stmt.setInt(2, discussion.getCategoryId());
            stmt.setString(3, discussion.getTitle());
            stmt.setString(4, discussion.getContent());
            stmt.setString(5, discussion.getModerationStatus().name());
            stmt.setInt(6, discussion.getViewCount());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseOperationException("Creating discussion failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    discussion.setId(generatedKeys.getLong(1));
                }
            }
            return discussion;
        } catch (SQLException e) {
            logger.error("Error creating discussion: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save discussion", e);
        }
    }

    @Override
    public boolean update(Discussion discussion) throws DatabaseOperationException {
        String sql = "UPDATE discussions SET category_id = ?, title = ?, content = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discussion.getCategoryId());
            stmt.setString(2, discussion.getTitle());
            stmt.setString(3, discussion.getContent());
            stmt.setLong(4, discussion.getId());
            stmt.setLong(5, discussion.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating discussion {}: {}", discussion.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update discussion", e);
        }
    }

    @Override
    public boolean updateModeration(Long discussionId, ModerationStatus status, String rejectionReason) throws DatabaseOperationException {
        String sql = "UPDATE discussions SET moderation_status = ?, rejection_reason = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, rejectionReason);
            stmt.setLong(3, discussionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error moderating discussion {}: {}", discussionId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update discussion moderation status", e);
        }
    }

    @Override
    public boolean incrementViewCount(Long id) throws DatabaseOperationException {
        String sql = "UPDATE discussions SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Could not increment discussion view count {}: {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM discussions WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting discussion {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete discussion", e);
        }
    }

    @Override
    public List<Discussion> findAll(int offset, int limit) throws DatabaseOperationException {
        return findApproved(offset, limit, null, null);
    }

    @Override
    public List<Discussion> findApproved(int offset, int limit, Integer categoryId, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE d.moderation_status = 'APPROVED' ");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND d.category_id = ? ");
            params.add(categoryId);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (d.title LIKE ? OR d.content LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
            params.add(term);
        }
        sql.append("ORDER BY d.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Discussion> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDiscussion(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding approved discussions: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve discussions", e);
        }
    }

    @Override
    public long countApproved(Integer categoryId, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM discussions d WHERE d.moderation_status = 'APPROVED' ");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND d.category_id = ? ");
            params.add(categoryId);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (d.title LIKE ? OR d.content LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
            params.add(term);
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting approved discussions: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count discussions", e);
        }
    }

    @Override
    public List<Discussion> findByUserId(Long userId, int offset, int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE d.user_id = ? ORDER BY d.created_at DESC LIMIT ? OFFSET ?";
        List<Discussion> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDiscussion(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding discussions by user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve user discussions", e);
        }
    }

    @Override
    public long countByUserId(Long userId) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM discussions WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting discussions for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count user discussions", e);
        }
    }

    @Override
    public List<Discussion> findAllForModeration(int offset, int limit, ModerationStatus status) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql.append("WHERE d.moderation_status = ? ");
            params.add(status.name());
        }
        sql.append("ORDER BY d.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Discussion> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDiscussion(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving discussions for moderation: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve discussions for moderation", e);
        }
    }

    @Override
    public long countForModeration(ModerationStatus status) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM discussions ");
        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql.append("WHERE moderation_status = ? ");
            params.add(status.name());
        }
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting moderation discussions: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count moderation discussions", e);
        }
    }

    @Override
    public List<Discussion> findRecent(int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE d.moderation_status = 'APPROVED' ORDER BY d.created_at DESC LIMIT ?";
        List<Discussion> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDiscussion(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving recent discussions: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve recent discussions", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM discussions";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting total discussions: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count discussions", e);
        }
    }

    private Discussion mapResultSetToDiscussion(ResultSet rs) throws SQLException {
        Discussion d = new Discussion();
        d.setId(rs.getLong("id"));
        d.setUserId(rs.getLong("user_id"));
        d.setAuthorName(rs.getString("author_name"));
        d.setAuthorUsername(rs.getString("author_username"));
        d.setCategoryId(rs.getInt("category_id"));
        d.setCategoryName(rs.getString("category_name"));
        d.setCategorySlug(rs.getString("category_slug"));
        d.setTitle(rs.getString("title"));
        d.setContent(rs.getString("content"));
        d.setModerationStatus(ModerationStatus.fromString(rs.getString("moderation_status")));
        d.setRejectionReason(rs.getString("rejection_reason"));
        d.setViewCount(rs.getInt("view_count"));
        d.setCommentCount(rs.getInt("comment_count"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        return d;
    }
}
