package com.gardenconnect.service.impl;

import com.gardenconnect.dao.*;
import com.gardenconnect.dao.impl.*;
import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.DashboardStats;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.ProjectStatus;
import com.gardenconnect.model.Role;
import com.gardenconnect.service.DashboardService;
public class DashboardServiceImpl implements DashboardService {

    private final UserDAO userDAO;
    private final TipDAO tipDAO;
    private final DiscussionDAO discussionDAO;
    private final CommentDAO commentDAO;
    private final GardeningProjectDAO projectDAO;

    public DashboardServiceImpl() {
        this(new UserDAOImpl(), new TipDAOImpl(), new DiscussionDAOImpl(), new CommentDAOImpl(), new GardeningProjectDAOImpl());
    }

    public DashboardServiceImpl(UserDAO userDAO, TipDAO tipDAO, DiscussionDAO discussionDAO,
                                CommentDAO commentDAO, GardeningProjectDAO projectDAO) {
        this.userDAO = userDAO;
        this.tipDAO = tipDAO;
        this.discussionDAO = discussionDAO;
        this.commentDAO = commentDAO;
        this.projectDAO = projectDAO;
    }

    @Override
    public DashboardStats getGardenerDashboardStats(Long userId) throws GardenConnectException {
        DashboardStats stats = new DashboardStats();
        stats.setUserTipsCount(tipDAO.countByUserId(userId));
        stats.setUserDiscussionsCount(discussionDAO.countByUserId(userId));
        stats.setUserActiveProjectsCount(projectDAO.countByUserId(userId, ProjectStatus.IN_PROGRESS));
        stats.setUserCompletedProjectsCount(projectDAO.countByUserId(userId, ProjectStatus.COMPLETED));
        stats.setTotalProjects(projectDAO.countByUserId(userId, null));
        return stats;
    }

    @Override
    public DashboardStats getAdminDashboardStats() throws GardenConnectException {
        DashboardStats stats = new DashboardStats();
        stats.setTotalUsers(userDAO.count());
        stats.setTotalGardeners(userDAO.countByRole(Role.GARDENER));
        stats.setTotalAdmins(userDAO.countByRole(Role.ADMIN));
        stats.setTotalTips(tipDAO.count());
        stats.setPendingModerationCount(tipDAO.countByStatus(ModerationStatus.PENDING));
        stats.setApprovedTipsCount(tipDAO.countByStatus(ModerationStatus.APPROVED));
        stats.setRejectedTipsCount(tipDAO.countByStatus(ModerationStatus.REJECTED));
        stats.setTotalDiscussions(discussionDAO.count());
        stats.setTotalComments(commentDAO.count());
        stats.setTotalProjects(projectDAO.count());

        // Analytical charts data for Chart.js
        stats.setTipsByCategory(tipDAO.getTipCountByCategory());
        stats.setProjectsByStatus(projectDAO.getProjectCountByStatus());

        return stats;
    }

    @Override
    public DashboardStats getPublicCommunityStats() throws GardenConnectException {
        DashboardStats stats = new DashboardStats();
        stats.setTotalGardeners(userDAO.countByRole(Role.GARDENER));
        stats.setApprovedTipsCount(tipDAO.countByStatus(ModerationStatus.APPROVED));
        stats.setTotalDiscussions(discussionDAO.countApproved(null, null));
        stats.setTotalProjects(projectDAO.count());
        return stats;
    }
}
