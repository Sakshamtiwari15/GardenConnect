package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.Role;
import com.gardenconnect.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for user management and authentication.
 */
public interface UserDAO extends BaseDAO<User, Long> {

    Optional<User> findByUsername(String username) throws DatabaseOperationException;

    Optional<User> findByEmail(String email) throws DatabaseOperationException;

    boolean updateProfile(User user) throws DatabaseOperationException;

    boolean updatePassword(Long userId, String newPasswordHash) throws DatabaseOperationException;

    boolean updateRole(Long userId, Role newRole) throws DatabaseOperationException;

    boolean updateStatus(Long userId, boolean isActive) throws DatabaseOperationException;

    List<User> findAll(int offset, int limit, Role roleFilter, String searchQuery) throws DatabaseOperationException;

    long count(Role roleFilter, String searchQuery) throws DatabaseOperationException;

    long countByRole(Role role) throws DatabaseOperationException;

    long countActive() throws DatabaseOperationException;
}
