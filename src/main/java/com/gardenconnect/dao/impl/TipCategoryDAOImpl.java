package com.gardenconnect.dao.impl;

import com.gardenconnect.config.DatabaseConfig;
import com.gardenconnect.dao.TipCategoryDAO;
import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.TipCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TipCategoryDAO.
 */
public class TipCategoryDAOImpl implements TipCategoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(TipCategoryDAOImpl.class);

    private static final String SELECT_BASE =
            "SELECT id, name, slug, description, icon, display_order FROM tip_categories ";

    @Override
    public List<TipCategory> findAll() throws DatabaseOperationException {
        String sql = SELECT_BASE + "ORDER BY display_order ASC, name ASC";
        List<TipCategory> categories = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            return categories;
        } catch (SQLException e) {
            logger.error("Error retrieving tip categories: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve tip categories", e);
        }
    }

    @Override
    public Optional<TipCategory> findById(Integer id) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding tip category by id {}: {}", id, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find tip category", e);
        }
    }

    @Override
    public Optional<TipCategory> findBySlug(String slug) throws DatabaseOperationException {
        String sql = SELECT_BASE + "WHERE slug = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding tip category by slug {}: {}", slug, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find tip category by slug", e);
        }
    }

    private TipCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        return new TipCategory(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("slug"),
                rs.getString("description"),
                rs.getString("icon"),
                rs.getInt("display_order")
        );
    }
}
