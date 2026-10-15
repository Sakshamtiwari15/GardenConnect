package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.UserDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Role;
import com.gardenconnect.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO.
 * Adheres to standard JDBC patterns with PreparedStatement and try-with-resources.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT id, username, email, password_hash, full_name, bio, location, " +
            "gardening_interests, role, is_active, created_at, updated_at FROM users ";

    @Override
    public Optional<User> findById(Long id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find user by id", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by username {}: {}", username, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find user by username", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find user by email", e);
        }
    }

    @Override
    public User create(User user) throws DatabaseOperationException {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, bio, location, gardening_interests, role, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getBio());
            stmt.setString(6, user.getLocation());
            stmt.setString(7, user.getGardeningInterests());
            stmt.setString(8, user.getRole().name());
            stmt.setBoolean(9, user.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseOperationException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseOperationException("Creating user failed, no ID obtained.");
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user {}: {}", user.getUsername(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to persist user to database", e);
        }
    }

    @Override
    public boolean update(User user) throws DatabaseOperationException {
        String sql = "UPDATE users SET full_name = ?, bio = ?, location = ?, gardening_interests = ?, role = ?, is_active = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getLocation());
            stmt.setString(4, user.getGardeningInterests());
            stmt.setString(5, user.getRole().name());
            stmt.setBoolean(6, user.isActive());
            stmt.setLong(7, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update user", e);
        }
    }

    @Override
    public boolean updateProfile(User user) throws DatabaseOperationException {
        String sql = "UPDATE users SET full_name = ?, bio = ?, location = ?, gardening_interests = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getBio());
            stmt.setString(3, user.getLocation());
            stmt.setString(4, user.getGardeningInterests());
            stmt.setLong(5, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user profile {}: {}", user.getId(), e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update profile", e);
        }
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) throws DatabaseOperationException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update user password", e);
        }
    }

    @Override
    public boolean updateRole(Long userId, Role newRole) throws DatabaseOperationException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole.name());
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating role for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update user role", e);
        }
    }

    @Override
    public boolean updateStatus(Long userId, boolean isActive) throws DatabaseOperationException {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating status for user {}: {}", userId, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update user active status", e);
        }
    }

    @Override
    public boolean delete(Long id) throws DatabaseOperationException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to delete user", e);
        }
    }

    @Override
    public List<User> findAll(int offset, int limit) throws DatabaseOperationException {
        return findAll(offset, limit, null, null);
    }

    @Override
    public List<User> findAll(int offset, int limit, Role roleFilter, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (roleFilter != null) {
            sql.append("AND role = ? ");
            params.add(roleFilter.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR email LIKE ? OR full_name LIKE ?) ");
            String term = "%" + searchQuery.trim() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }
        sql.append("ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error listing users: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve user list", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        return count(null, null);
    }

    @Override
    public long count(Role roleFilter, String searchQuery) throws DatabaseOperationException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (roleFilter != null) {
            sql.append("AND role = ? ");
            params.add(roleFilter.name());
        }
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (username LIKE ? OR email LIKE ? OR full_name LIKE ?) ");
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
            logger.error("Error counting users: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count users", e);
        }
    }

    @Override
    public long countByRole(Role role) throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users by role {}: {}", role, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count users by role", e);
        }
    }

    @Override
    public long countActive() throws DatabaseOperationException {
        String sql = "SELECT COUNT(*) FROM users WHERE is_active = TRUE";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active users: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to count active users", e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setBio(rs.getString("bio"));
        user.setLocation(rs.getString("location"));
        user.setGardeningInterests(rs.getString("gardening_interests"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
