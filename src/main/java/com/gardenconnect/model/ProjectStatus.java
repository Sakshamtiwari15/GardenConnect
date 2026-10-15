package com.gardenconnect.model;

/**
 * Status lifecycle enum for personal gardening projects.
 */
public enum ProjectStatus {
    PLANNED("Planned", "bg-secondary", "Project is in planning and preparation stage"),
    IN_PROGRESS("In Progress", "bg-primary", "Active planting, cultivation, or development"),
    COMPLETED("Completed", "bg-success", "Harvested or finished successfully"),
    ON_HOLD("On Hold", "bg-warning text-dark", "Temporarily paused or awaiting resources");

    private final String displayName;
    private final String badgeClass;
    private final String description;

    ProjectStatus(String displayName, String badgeClass, String description) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getDescription() {
        return description;
    }

    public static ProjectStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return PLANNED;
        }
        try {
            return ProjectStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PLANNED;
        }
    }
}
