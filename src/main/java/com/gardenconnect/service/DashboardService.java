package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.DashboardStats;

/**
 * Service aggregating metrics, key performance indicators, and chart data
 * for public, gardener, and admin dashboards.
 */
public interface DashboardService {

    DashboardStats getGardenerDashboardStats(Long userId) throws GardenConnectException;

    DashboardStats getAdminDashboardStats() throws GardenConnectException;

    DashboardStats getPublicCommunityStats() throws GardenConnectException;
}
