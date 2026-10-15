package com.gardenconnect.dao;

import com.gardenconnect.exception.DatabaseOperationException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic Base Data Access Object interface.
 * Demonstrates Generics (T as Entity, ID as Primary Key) and Abstraction.
 *
 * @param <T>  The domain entity type.
 * @param <ID> The primary key type.
 */
public interface BaseDAO<T, ID extends Serializable> {

    /**
     * Finds an entity by its unique primary key.
     */
    Optional<T> findById(ID id) throws DatabaseOperationException;

    /**
     * Persists a new entity to the database.
     */
    T create(T entity) throws DatabaseOperationException;

    /**
     * Updates an existing entity in the database.
     */
    boolean update(T entity) throws DatabaseOperationException;

    /**
     * Deletes an entity by primary key.
     */
    boolean delete(ID id) throws DatabaseOperationException;

    /**
     * Retrieves all entities with pagination.
     */
    List<T> findAll(int offset, int limit) throws DatabaseOperationException;

    /**
     * Counts the total number of records.
     */
    long count() throws DatabaseOperationException;
}
