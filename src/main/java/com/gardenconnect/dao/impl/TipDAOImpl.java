package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.TipDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * JDBC implementation of TipDAO with SQL joins for relational author and category data.
 */
public class TipDAOImpl implements TipDAO {

    private static final Logger logger = LoggerFactory.getLogger(TipDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT t.id, t.user_id, u.full_name AS author_name, u.username AS author_username, " +
            "       t.category_id, c.name AS category_name, c.slug AS category_slug, " +
            "       t.title, t.summary, t.content, t.image_url, t.moderation_status, " +
            "       t.rejection_reason, t.view_count, t.created_at, t.updated_at " +
            "FROM tips t " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN tip_categories c ON t.category_id = c.id ";

    @Override
    public Optional<Tip> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE t.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTip(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding tip by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find tip by id", e);
        }
    }

    @Override
    public Tip create(Tip tip) throws DatabaseOperationException {
        String sql = "INSERT INTO tips (user_id, category_id, title, summary, content, image_url, moderation_status, view_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, tip.getUserId());
            stmt.setInt(2, tip.getCategoryId());
            stmt.setString(3, tip.getTitle());
            stmt.setString(4, tip.getSummary());
            stmt.setString(5, tip.getContent());
            stmt.setString(6, tip.getImageUrl());
            stmt.setString(7, tip.getModerationStatus().name());
            stmt.setInt(8, tip.getViewCount());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseOperationException("Creating tip failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tip.setId(generatedKeys.getLong(1));
                }
            }
            return tip;
        } catch (SQLException e) {
            logger.error("Error creating tip {}: {}", tip.getTitle(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to save gardening tip", e);
        }
    }

    @Override
    public boolean update(Tip tip) throws DatabaseOperationException {
        String sql = "UPDATE tips SET category_id = ?, title = ?, summary = ?, content = ?, image_url = ?, " +
                     "moderation_status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tip.getCategoryId());
            stmt.setString(2, tip.getTitle());
            stmt.setString(3, tip.getSummary());
            stmt.setString(4, tip.getContent());
            stmt.setString(5, tip.getImageUrl());
            stmt.setString(6, tip.getModerationStatus().name());
            stmt.setLong(7, tip.getId());
            stmt.setLong(8, tip.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating tip {}: {}", tip.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update gardening tip", e);
        }
    }

    @Override
    public boolean updateModeration(Long tipId, ModerationStatus status, String rejectionReason) throws DatabaseOperationException {
        String sql = "UPDATE tips SET moderation_status = ?, rejection_reason = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, rejectionReason);
            stmt.setLong(3, tipId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error moderating tip {}: {}", tipId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update tip moderation status", e);
        }
    }

    @Override
    public boolean incrementViewCount(Long tipId) throws DatabaseOperationException {
        String sql = "UPDATE tips SET view_count = view_count + 1 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tipId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.warn("Could not increment view count for tip {}: {}", tipId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM tips WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting tip {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete tip", e);
        }
    }

    @Override
    public List<Tip> findAll(int offset, int limit) throws DatabaseOperationException {
        return findApproved(offset, limit, null, null, "newest");
    }

    @Override
    public List<Tip> findApproved(int offset, int limit, Integer categoryId, String searchQuery, String sortBy) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        sql.append("WHERE t.moderation_status = 'APPROVED' ");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND t.category_id = ? ");
            params.add(categoryId);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.title LIKE ? OR t.summary LIKE ? OR t.content LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if ("popular".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY t.view_count DESC, t.created_at DESC ");
        } else if ("oldest".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY t.created_at ASC ");
        } else {
            sql.append("ORDER BY t.created_at DESC ");
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Tip> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTip(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding approved tips: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve approved tips", e);
        }
    }

    @Override
    public long countApproved(Integer categoryId, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tips t WHERE t.moderation_status = 'APPROVED' ");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND t.category_id = ? ");
            params.add(categoryId);
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.title LIKE ? OR t.summary LIKE ? OR t.content LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
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
            logger.error("Error counting approved tips: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count approved tips", e);
        }
    }

    @Override
    public List<Tip> findByUserId(Long userId, int offset, int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE t.user_id = ? ORDER BY t.created_at DESC LIMIT ? OFFSET ?";
        List<Tip> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTip(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding tips by user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve user tips", e);
        }
    }

    @Override
    public long countByUserId(Long userId) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM tips WHERE user_id = ?";
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
            logger.error("Error counting tips for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count user tips", e);
        }
    }

    @Override
    public List<Tip> findAllForModeration(int offset, int limit, ModerationStatus statusFilter, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (statusFilter != null) {
            sql.append("AND t.moderation_status = ? ");
            params.add(statusFilter.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.title LIKE ? OR u.username LIKE ? OR u.full_name LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }
        sql.append("ORDER BY t.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Tip> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTip(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving tips for moderation: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve tips for moderation", e);
        }
    }

    @Override
    public long countForModeration(ModerationStatus statusFilter, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tips t JOIN users u ON t.user_id = u.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (statusFilter != null) {
            sql.append("AND t.moderation_status = ? ");
            params.add(statusFilter.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (t.title LIKE ? OR u.username LIKE ? OR u.full_name LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
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
            logger.error("Error counting tips for moderation: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count moderation tips", e);
        }
    }

    @Override
    public List<Tip> findRecentApproved(int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE t.moderation_status = 'APPROVED' ORDER BY t.created_at DESC LIMIT ?";
        List<Tip> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTip(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding recent approved tips: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve recent tips", e);
        }
    }

    @Override
    public long countByStatus(ModerationStatus status) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM tips WHERE moderation_status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting tips by status {}: {}", status, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count tips by status", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM tips";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting total tips: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count total tips", e);
        }
    }

    @Override
    public Map<String, Long> getTipCountByCategory() throws DatabaseOperationException {
        String sql = "SELECT c.name, COUNT(t.id) as total " +
                     "FROM tip_categories c " +
                     "LEFT JOIN tips t ON c.id = t.category_id AND t.moderation_status = 'APPROVED' " +
                     "GROUP BY c.id, c.name ORDER BY total DESC";
        Map<String, Long> stats = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("name"), rs.getLong("total"));
            }
            return stats;
        } catch (SQLException e) {
            logger.error("Error grouping tips by category: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to get tip category statistics", e);
        }
    }

    private Tip mapResultSetToTip(ResultSet rs) throws SQLException {
        Tip tip = new Tip();
        tip.setId(rs.getLong("id"));
        tip.setUserId(rs.getLong("user_id"));
        tip.setAuthorName(rs.getString("author_name"));
        tip.setAuthorUsername(rs.getString("author_username"));
        tip.setCategoryId(rs.getInt("category_id"));
        tip.setCategoryName(rs.getString("category_name"));
        tip.setCategorySlug(rs.getString("category_slug"));
        tip.setTitle(rs.getString("title"));
        tip.setSummary(rs.getString("summary"));
        tip.setContent(rs.getString("content"));
        tip.setImageUrl(rs.getString("image_url"));
        tip.setModerationStatus(ModerationStatus.fromString(rs.getString("moderation_status")));
        tip.setRejectionReason(rs.getString("rejection_reason"));
        tip.setViewCount(rs.getInt("view_count"));
        tip.setCreatedAt(rs.getTimestamp("created_at"));
        tip.setUpdatedAt(rs.getTimestamp("updated_at"));
        return tip;
    }
}
