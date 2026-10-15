package com.gardenconnect.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object (DTO) aggregating analytical and statistical metrics
 * for Gardener and Administrator dashboards.
 * Demonstrates Collections (Map, HashMap).
 */
public class DashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    // Platform-wide metrics (Admin)
    private long totalUsers;
    private long totalGardeners;
    private long totalAdmins;
    private long totalTips;
    private long pendingModerationCount;
    private long approvedTipsCount;
    private long rejectedTipsCount;
    private long totalDiscussions;
    private long totalComments;
    private long totalProjects;

    // Gardener-specific metrics
    private long userTipsCount;
    private long userApprovedTipsCount;
    private long userDiscussionsCount;
    private long userActiveProjectsCount;
    private long userCompletedProjectsCount;

    // Analytical distributions for Chart.js
    private Map<String, Long> tipsByCategory = new HashMap<>();
    private Map<String, Long> projectsByStatus = new HashMap<>();
    private Map<String, Long> userRegistrationTrend = new HashMap<>();

    public DashboardStats() {}

    // Admin Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalGardeners() {
        return totalGardeners;
    }

    public void setTotalGardeners(long totalGardeners) {
        this.totalGardeners = totalGardeners;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalTips() {
        return totalTips;
    }

    public void setTotalTips(long totalTips) {
        this.totalTips = totalTips;
    }

    public long getPendingModerationCount() {
        return pendingModerationCount;
    }

    public void setPendingModerationCount(long pendingModerationCount) {
        this.pendingModerationCount = pendingModerationCount;
    }

    public long getApprovedTipsCount() {
        return approvedTipsCount;
    }

    public void setApprovedTipsCount(long approvedTipsCount) {
        this.approvedTipsCount = approvedTipsCount;
    }

    public long getRejectedTipsCount() {
        return rejectedTipsCount;
    }

    public void setRejectedTipsCount(long rejectedTipsCount) {
        this.rejectedTipsCount = rejectedTipsCount;
    }

    public long getTotalDiscussions() {
        return totalDiscussions;
    }

    public void setTotalDiscussions(long totalDiscussions) {
        this.totalDiscussions = totalDiscussions;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    // Gardener Getters and Setters
    public long getUserTipsCount() {
        return userTipsCount;
    }

    public void setUserTipsCount(long userTipsCount) {
        this.userTipsCount = userTipsCount;
    }

    public long getUserApprovedTipsCount() {
        return userApprovedTipsCount;
    }

    public void setUserApprovedTipsCount(long userApprovedTipsCount) {
        this.userApprovedTipsCount = userApprovedTipsCount;
    }

    public long getUserDiscussionsCount() {
        return userDiscussionsCount;
    }

    public void setUserDiscussionsCount(long userDiscussionsCount) {
        this.userDiscussionsCount = userDiscussionsCount;
    }

    public long getUserActiveProjectsCount() {
        return userActiveProjectsCount;
    }

    public void setUserActiveProjectsCount(long userActiveProjectsCount) {
        this.userActiveProjectsCount = userActiveProjectsCount;
    }

    public long getUserCompletedProjectsCount() {
        return userCompletedProjectsCount;
    }

    public void setUserCompletedProjectsCount(long userCompletedProjectsCount) {
        this.userCompletedProjectsCount = userCompletedProjectsCount;
    }

    // Chart Data Maps
    public Map<String, Long> getTipsByCategory() {
        return tipsByCategory;
    }

    public void setTipsByCategory(Map<String, Long> tipsByCategory) {
        this.tipsByCategory = tipsByCategory;
    }

    public Map<String, Long> getProjectsByStatus() {
        return projectsByStatus;
    }

    public void setProjectsByStatus(Map<String, Long> projectsByStatus) {
        this.projectsByStatus = projectsByStatus;
    }

    public Map<String, Long> getUserRegistrationTrend() {
        return userRegistrationTrend;
    }

    public void setUserRegistrationTrend(Map<String, Long> userRegistrationTrend) {
        this.userRegistrationTrend = userRegistrationTrend;
    }
}
