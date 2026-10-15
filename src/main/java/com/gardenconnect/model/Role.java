package com.gardenconnect.model;

/**
 * Defines the access roles within the GardenConnect platform.
 * Supports role-based access control (RBAC).
 */
public enum Role {
    ADMIN("Administrator", "Full administrative access to moderation, users, and platform settings"),
    GARDENER("Gardener", "Community member with access to tips, discussions, and personal projects");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return GARDENER;
        }
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GARDENER;
        }
    }
}
