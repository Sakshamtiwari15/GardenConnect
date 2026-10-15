package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;
import com.gardenconnect.model.TipCategory;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for tip categories taxonomy.
 */
public interface TipCategoryDAO {

    List<TipCategory> findAll() throws DatabaseOperationException;

    Optional<TipCategory> findById(Integer id) throws DatabaseOperationException;

    Optional<TipCategory> findBySlug(String slug) throws DatabaseOperationException;
}
