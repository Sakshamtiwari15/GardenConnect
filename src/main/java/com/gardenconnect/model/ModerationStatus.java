package com.gardenconnect.model;

/**
 * State lifecycle enum for community submitted tips and discussions.
 */
public enum ModerationStatus {
    PENDING("Pending Review", "badge-warning", "Awaiting moderator review before public release"),
    APPROVED("Approved", "badge-success", "Content is public and visible to community"),
    REJECTED("Rejected", "badge-danger", "Content violates guidelines and is hidden from public");

    private final String label;
    private final String badgeClass;
    private final String description;

    ModerationStatus(String label, String badgeClass, String description) {
        this.label = label;
        this.badgeClass = badgeClass;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public String getDescription() {
        return description;
    }

    public static ModerationStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return PENDING;
        }
        try {
            return ModerationStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
