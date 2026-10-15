package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.DiscussionCategory;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for discussion taxonomy.
 */
public interface DiscussionCategoryDAO {

    List<DiscussionCategory> findAll() throws DatabaseOperationException;

    Optional<DiscussionCategory> findById(Integer id) throws DatabaseOperationException;

    Optional<DiscussionCategory> findBySlug(String slug) throws DatabaseOperationException;
}
