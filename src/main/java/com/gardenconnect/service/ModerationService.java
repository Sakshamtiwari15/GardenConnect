package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.Discussion;
import com.gardenconnect.model.ModerationStatus;
import com.gardenconnect.model.Tip;
import com.gardenconnect.util.PaginationUtil;

/**
 * Service managing content moderation queues and approval/rejection workflows.
 */
public interface ModerationService {

    PaginationUtil<Tip> getTipsForModeration(int page, int pageSize, ModerationStatus statusFilter, String searchQuery) throws GardenConnectException;

    boolean approveTip(Long adminId, String adminUsername, Long tipId) throws GardenConnectException;

    boolean rejectTip(Long adminId, String adminUsername, Long tipId, String reason) throws GardenConnectException;

    PaginationUtil<Discussion> getDiscussionsForModeration(int page, int pageSize, ModerationStatus statusFilter) throws GardenConnectException;

    boolean approveDiscussion(Long adminId, String adminUsername, Long discussionId) throws GardenConnectException;

    boolean rejectDiscussion(Long adminId, String adminUsername, Long discussionId, String reason) throws GardenConnectException;

    long getPendingTipsCount() throws GardenConnectException;

    long getPendingDiscussionsCount() throws GardenConnectException;
}
