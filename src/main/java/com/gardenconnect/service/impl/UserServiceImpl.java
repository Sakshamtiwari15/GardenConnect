package com.gardenconnect.service.impl;

import com.gardenconnect.concurrency.AsyncActivityProcessor;
import com.gardenconnect.dao.UserDAO;
import com.gardenconnect.dao.impl.UserDAOImpl;
import com.gardenconnect.exception.*;
import com.gardenconnect.model.ActivityLog;
import com.gardenconnect.model.Role;
import com.gardenconnect.model.User;
import com.gardenconnect.security.InputSanitizer;
import com.gardenconnect.security.PasswordHasher;
import com.gardenconnect.service.UserService;
import com.gardenconnect.util.PaginationUtil;
import com.gardenconnect.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business service implementation for User authentication and administrative management.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;
    private final AsyncActivityProcessor asyncActivityProcessor;

    public UserServiceImpl() {
        this(new UserDAOImpl(), AsyncActivityProcessor.getInstance());
    }

    public UserServiceImpl(UserDAO userDAO, AsyncActivityProcessor asyncActivityProcessor) {
        this.userDAO = userDAO;
        this.asyncActivityProcessor = asyncActivityProcessor;
    }

    @Override
    public User register(String username, String email, String password, String fullName) throws GardenConnectException {
        Map<String, String> errors = new HashMap<>();

        // Validation
        if (!ValidationUtil.isValidUsername(username)) {
            errors.put("username", "Username must be 3-30 alphanumeric characters or underscores.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Please provide a valid email address.");
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            errors.put("password", "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.");
        }
        if (!ValidationUtil.isWithinLength(fullName, 2, 100)) {
            errors.put("fullName", "Full name must be between 2 and 100 characters.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Registration validation failed", errors);
        }

        // Uniqueness checks
        if (userDAO.findByUsername(username.trim()).isPresent()) {
            throw new DuplicateResourceException("User", "username", username.trim());
        }
        if (userDAO.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new DuplicateResourceException("User", "email", email.trim().toLowerCase());
        }

        // Hash password and sanitize strings
        String hashedPassword = PasswordHasher.hashPassword(password);
        User newUser = new User(
                username.trim(),
                email.trim().toLowerCase(),
                hashedPassword,
                InputSanitizer.escapeHtml(fullName.trim()),
                Role.GARDENER // Registration ALWAYS assigns GARDENER role
        );

        User created = userDAO.create(newUser);
        logger.info("New gardener registered successfully: {}", created.getUsername());

        // Asynchronous audit logging
        asyncActivityProcessor.submitLog(new ActivityLog(
                created.getId(),
                created.getUsername(),
                "USER_REGISTRATION",
                "USER",
                created.getId(),
                "Registered new gardener account",
                "127.0.0.1"
        ));

        return created;
    }

    @Override
    public User login(String usernameOrEmail, String password, String ipAddress) throws GardenConnectException {
        if (!ValidationUtil.isNotEmpty(usernameOrEmail) || !ValidationUtil.isNotEmpty(password)) {
            throw new ValidationException("Username and password are required.");
        }

        Optional<User> userOpt;
        if (usernameOrEmail.contains("@")) {
            userOpt = userDAO.findByEmail(usernameOrEmail.trim().toLowerCase());
        } else {
            userOpt = userDAO.findByUsername(usernameOrEmail.trim());
        }

        if (userOpt.isEmpty()) {
            logger.warn("Failed login attempt for non-existent user: {}", usernameOrEmail);
            throw new ValidationException("Invalid username/email or password.");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            logger.warn("Deactivated user attempted login: {}", user.getUsername());
            throw new UnauthorizedActionException("Your account has been deactivated. Please contact an administrator.");
        }

        if (!PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            logger.warn("Failed login password check for user: {}", user.getUsername());
            throw new ValidationException("Invalid username/email or password.");
        }

        logger.info("User logged in successfully: {} [{}]", user.getUsername(), user.getRole());

        // Asynchronous audit logging
        asyncActivityProcessor.submitLog(new ActivityLog(
                user.getId(),
                user.getUsername(),
                "USER_LOGIN",
                "USER",
                user.getId(),
                "User logged in from " + ipAddress,
                ipAddress
        ));

        return user;
    }

    @Override
    public User getUserById(Long id) throws ResourceNotFoundException, GardenConnectException {
        return userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public boolean updateProfile(Long userId, String fullName, String bio, String location, String gardeningInterests) throws GardenConnectException {
        User user = getUserById(userId);

        if (!ValidationUtil.isWithinLength(fullName, 2, 100)) {
            throw new ValidationException("fullName", "Full name must be between 2 and 100 characters.");
        }

        user.setFullName(InputSanitizer.escapeHtml(fullName.trim()));
        user.setBio(InputSanitizer.escapeHtml(bio != null ? bio.trim() : null));
        user.setLocation(InputSanitizer.escapeHtml(location != null ? location.trim() : null));
        user.setGardeningInterests(InputSanitizer.escapeHtml(gardeningInterests != null ? gardeningInterests.trim() : null));

        boolean updated = userDAO.updateProfile(user);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    user.getUsername(),
                    "PROFILE_UPDATED",
                    "USER",
                    userId,
                    "Updated profile information",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) throws GardenConnectException {
        User user = getUserById(userId);

        if (!PasswordHasher.checkPassword(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("currentPassword", "Incorrect current password.");
        }

        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new ValidationException("newPassword", "New password must be at least 8 characters and contain uppercase, lowercase, digit, and special character.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("confirmPassword", "New password and confirmation do not match.");
        }

        String newHash = PasswordHasher.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(userId, newHash);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    userId,
                    user.getUsername(),
                    "PASSWORD_CHANGED",
                    "USER",
                    userId,
                    "Changed account password",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public PaginationUtil<User> listUsers(int page, int pageSize, Role roleFilter, String searchQuery) throws GardenConnectException {
        int offset = PaginationUtil.calculateOffset(page, pageSize);
        List<User> items = userDAO.findAll(offset, pageSize, roleFilter, searchQuery);
        long total = userDAO.count(roleFilter, searchQuery);
        return new PaginationUtil<>(items, page, pageSize, total);
    }

    @Override
    public boolean updateUserRole(Long adminId, Long targetUserId, Role newRole) throws GardenConnectException {
        if (adminId.equals(targetUserId)) {
            throw new UnauthorizedActionException("Administrators cannot change their own role.");
        }

        User targetUser = getUserById(targetUserId);

        // Prevent demoting the last admin
        if (targetUser.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userDAO.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new UnauthorizedActionException("Cannot demote the last remaining administrator.");
            }
        }

        boolean updated = userDAO.updateRole(targetUserId, newRole);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    "admin",
                    "USER_ROLE_CHANGED",
                    "USER",
                    targetUserId,
                    "Changed role of user '" + targetUser.getUsername() + "' to " + newRole.name(),
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean toggleUserStatus(Long adminId, Long targetUserId, boolean isActive) throws GardenConnectException {
        if (adminId.equals(targetUserId)) {
            throw new UnauthorizedActionException("Administrators cannot deactivate their own account.");
        }

        User targetUser = getUserById(targetUserId);

        // Prevent deactivating the last active admin
        if (!isActive && targetUser.getRole() == Role.ADMIN) {
            long activeAdmins = userDAO.countByRole(Role.ADMIN);
            if (activeAdmins <= 1) {
                throw new UnauthorizedActionException("Cannot deactivate the last active administrator.");
            }
        }

        boolean updated = userDAO.updateStatus(targetUserId, isActive);
        if (updated) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    "admin",
                    "USER_STATUS_TOGGLED",
                    "USER",
                    targetUserId,
                    (isActive ? "Activated" : "Deactivated") + " user '" + targetUser.getUsername() + "'",
                    "127.0.0.1"
            ));
        }
        return updated;
    }

    @Override
    public boolean deleteUser(Long adminId, Long targetUserId) throws GardenConnectException {
        if (adminId.equals(targetUserId)) {
            throw new UnauthorizedActionException("Administrators cannot delete their own account.");
        }

        User targetUser = getUserById(targetUserId);
        if (targetUser.getRole() == Role.ADMIN) {
            long adminCount = userDAO.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new UnauthorizedActionException("Cannot delete the last remaining administrator.");
            }
        }

        boolean deleted = userDAO.delete(targetUserId);
        if (deleted) {
            asyncActivityProcessor.submitLog(new ActivityLog(
                    adminId,
                    "admin",
                    "USER_DELETED",
                    "USER",
                    targetUserId,
                    "Deleted user account '" + targetUser.getUsername() + "'",
                    "127.0.0.1"
            ));
        }
        return deleted;
    }

    @Override
    public User createUserByAdmin(Long adminId, String username, String email, String password, String fullName, Role role) throws GardenConnectException {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtil.isValidUsername(username)) {
            errors.put("username", "Username must be 3-30 alphanumeric characters or underscores.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Please provide a valid email address.");
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            errors.put("password", "Password must be at least 8 characters with upper, lower, digit, and special char.");
        }
        if (!ValidationUtil.isWithinLength(fullName, 2, 100)) {
            errors.put("fullName", "Full name must be between 2 and 100 characters.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("User creation validation failed", errors);
        }

        if (userDAO.findByUsername(username.trim()).isPresent()) {
            throw new DuplicateResourceException("User", "username", username.trim());
        }
        if (userDAO.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new DuplicateResourceException("User", "email", email.trim().toLowerCase());
        }

        User newUser = new User(
                username.trim(),
                email.trim().toLowerCase(),
                PasswordHasher.hashPassword(password),
                InputSanitizer.escapeHtml(fullName.trim()),
                role != null ? role : Role.GARDENER
        );

        User created = userDAO.create(newUser);
        asyncActivityProcessor.submitLog(new ActivityLog(
                adminId,
                "admin",
                "USER_CREATED_BY_ADMIN",
                "USER",
                created.getId(),
                "Created new user '" + created.getUsername() + "' with role " + created.getRole().name(),
                "127.0.0.1"
        ));

        return created;
    }
}
