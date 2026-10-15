package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.exception.ResourceNotFoundException;
import com.gardenconnect.model.Role;
import com.gardenconnect.model.User;
import com.gardenconnect.util.PaginationUtil;

/**
 * Service interface managing user authentication, profiles, and administration.
 */
public interface UserService {

    User register(String username, String email, String password, String fullName) throws GardenConnectException;

    User login(String usernameOrEmail, String password, String ipAddress) throws GardenConnectException;

    User getUserById(Long id) throws ResourceNotFoundException, GardenConnectException;

    boolean updateProfile(Long userId, String fullName, String bio, String location, String gardeningInterests) throws GardenConnectException;

    boolean changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) throws GardenConnectException;

    PaginationUtil<User> listUsers(int page, int pageSize, Role roleFilter, String searchQuery) throws GardenConnectException;

    boolean updateUserRole(Long adminId, Long targetUserId, Role newRole) throws GardenConnectException;

    boolean toggleUserStatus(Long adminId, Long targetUserId, boolean isActive) throws GardenConnectException;

    boolean deleteUser(Long adminId, Long targetUserId) throws GardenConnectException;

    User createUserByAdmin(Long adminId, String username, String email, String password, String fullName, Role role) throws GardenConnectException;
}
