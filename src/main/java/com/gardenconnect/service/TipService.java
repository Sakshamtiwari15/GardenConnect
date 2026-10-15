package com.gardenconnect.service;

import com.gardenconnect.exception.GardenConnectException;
import com.gardenconnect.model.Tip;
import com.gardenconnect.model.TipCategory;
import com.gardenconnect.util.PaginationUtil;

import java.util.List;

/**
 * Service managing gardening tip authoring, browsing, and discovery.
 */
public interface TipService {

    Tip createTip(Long userId, String username, Integer categoryId, String title, String summary, String content, String imageUrl) throws GardenConnectException;

    boolean updateTip(Long tipId, Long userId, Integer categoryId, String title, String summary, String content, String imageUrl) throws GardenConnectException;

    boolean deleteTip(Long tipId, Long userId, boolean isAdmin) throws GardenConnectException;

    Tip getTipDetails(Long tipId, boolean incrementViews) throws GardenConnectException;

    PaginationUtil<Tip> browseApprovedTips(int page, int pageSize, Integer categoryId, String searchQuery, String sortBy) throws GardenConnectException;

    PaginationUtil<Tip> getGardenerTips(Long userId, int page, int pageSize) throws GardenConnectException;

    List<TipCategory> getAllCategories() throws GardenConnectException;

    List<Tip> getRecentFeaturedTips(int limit) throws GardenConnectException;
}
