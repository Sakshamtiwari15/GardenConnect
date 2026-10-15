package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.GardeningProjectDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.GardeningProject;
import com.gardenconnect.model.ProjectStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * JDBC implementation of GardeningProjectDAO.
 */
public class GardeningProjectDAOImpl implements GardeningProjectDAO {

    private static final Logger logger = LoggerFactory.getLogger(GardeningProjectDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT id, user_id, title, description, plant_crop_type, start_date, " +
            "       target_completion_date, status, progress_percentage, notes, created_at, updated_at " +
            "FROM gardening_projects ";

    @Override
    public Optional<GardeningProject> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding gardening project by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find gardening project", e);
        }
    }

    @Override
    public GardeningProject create(GardeningProject project) throws DatabaseOperationException {
        String sql = "INSERT INTO gardening_projects (user_id, title, description, plant_crop_type, " +
                     "start_date, target_completion_date, status, progress_percentage, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, project.getUserId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getDescription());
            stmt.setString(4, project.getPlantCropType());
            stmt.setDate(5, project.getStartDate());
            stmt.setDate(6, project.getTargetCompletionDate());
            stmt.setString(7, project.getStatus().name());
            stmt.setInt(8, project.getProgressPercentage());
            stmt.setString(9, project.getNotes());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DatabaseOperationException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getLong(1));
                }
            }
            return project;
        } catch (SQLException e) {
            logger.error("Error creating gardening project {}: {}", project.getTitle(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to persist gardening project", e);
        }
    }

    @Override
    public boolean update(GardeningProject project) throws DatabaseOperationException {
        String sql = "UPDATE gardening_projects SET title = ?, description = ?, plant_crop_type = ?, " +
                     "start_date = ?, target_completion_date = ?, status = ?, progress_percentage = ?, notes = ? " +
                     "WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, project.getTitle());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getPlantCropType());
            stmt.setDate(4, project.getStartDate());
            stmt.setDate(5, project.getTargetCompletionDate());
            stmt.setString(6, project.getStatus().name());
            stmt.setInt(7, project.getProgressPercentage());
            stmt.setString(8, project.getNotes());
            stmt.setLong(9, project.getId());
            stmt.setLong(10, project.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating gardening project {}: {}", project.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update gardening project", e);
        }
    }

    @Override
    public boolean updateProgress(Long id, Long userId, int progress, ProjectStatus status) throws DatabaseOperationException {
        String sql = "UPDATE gardening_projects SET progress_percentage = ?, status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progress);
            stmt.setString(2, status.name());
            stmt.setLong(3, id);
            stmt.setLong(4, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating progress for project {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update project progress", e);
        }
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM gardening_projects WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting gardening project {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete project", e);
        }
    }

    @Override
    public boolean delete(Long id, Long userId) throws DatabaseOperationException {
        String sql = "DELETE FROM gardening_projects WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting gardening project {} for user {}: {}", id, userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete gardening project", e);
        }
    }

    @Override
    public List<GardeningProject> findAll(int offset, int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "ORDER BY updated_at DESC LIMIT ? OFFSET ?";
        List<GardeningProject> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving all projects: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve gardening projects", e);
        }
    }

    @Override
    public List<GardeningProject> findByUserId(Long userId, int offset, int limit, ProjectStatus statusFilter) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (statusFilter != null) {
            sql.append("AND status = ? ");
            params.add(statusFilter.name());
        }
        sql.append("ORDER BY updated_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<GardeningProject> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding projects for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve user projects", e);
        }
    }

    @Override
    public long countByUserId(Long userId, ProjectStatus statusFilter) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM gardening_projects WHERE user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (statusFilter != null) {
            sql.append("AND status = ? ");
            params.add(statusFilter.name());
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
            logger.error("Error counting projects for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count user projects", e);
        }
    }

    @Override
    public List<GardeningProject> findRecentByUserId(Long userId, int limit) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE user_id = ? ORDER BY updated_at DESC LIMIT ?";
        List<GardeningProject> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error retrieving recent projects for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve recent projects", e);
        }
    }

    @Override
    public Map<String, Long> getProjectCountByStatus() throws DatabaseOperationException {
        String sql = "SELECT status, COUNT(*) as count FROM gardening_projects GROUP BY status";
        Map<String, Long> map = new LinkedHashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getLong("count"));
            }
            return map;
        } catch (SQLException e) {
            logger.error("Error getting project counts by status: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to get project status counts", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM gardening_projects";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting total gardening projects: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count projects", e);
        }
    }

    private GardeningProject mapResultSetToProject(ResultSet rs) throws SQLException {
        GardeningProject project = new GardeningProject();
        project.setId(rs.getLong("id"));
        project.setUserId(rs.getLong("user_id"));
        project.setTitle(rs.getString("title"));
        project.setDescription(rs.getString("description"));
        project.setPlantCropType(rs.getString("plant_crop_type"));
        project.setStartDate(rs.getDate("start_date"));
        project.setTargetCompletionDate(rs.getDate("target_completion_date"));
        project.setStatus(ProjectStatus.fromString(rs.getString("status")));
        project.setProgressPercentage(rs.getInt("progress_percentage"));
        project.setNotes(rs.getString("notes"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        project.setUpdatedAt(rs.getTimestamp("updated_at"));
        return project;
    }
}
