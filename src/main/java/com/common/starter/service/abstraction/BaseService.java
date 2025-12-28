package com.common.starter.service.abstraction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Generic interface for service layer operations.
 * Provides standard CRUD operations for entities.
 *
 * @param <T>  The entity type
 * @param <ID> The entity's identifier type
 */
public interface BaseService<T, ID extends Serializable> {

    /**
     * Creates a new entity.
     *
     * @param entity The entity to create
     * @return The created entity
     */
    T create(T entity);

    /**
     * Updates an existing entity.
     *
     * @param id     The entity ID
     * @param entity The updated entity data
     * @return The updated entity
     */
    T update(ID id, T entity);

    /**
     * Partially updates an existing entity.
     *
     * @param id            The entity ID
     * @param partialEntity The partial entity data
     * @return The updated entity
     */
    T partialUpdate(ID id, T partialEntity);

    /**
     * Deletes an entity by ID.
     *
     * @param id The entity ID
     */
    void delete(ID id);

    /**
     * Finds an entity by ID.
     *
     * @param id The entity ID
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Finds an entity by ID or throws an exception.
     *
     * @param id The entity ID
     * @return The entity
     * @throws com.common.starter.service.exception.ResourceNotFoundException if not
     *                                                                        found
     */
    T findByIdOrThrow(ID id);

    /**
     * Finds all entities.
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Finds all entities with pagination.
     *
     * @param pageable Pagination information
     * @return Page of entities
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Checks if an entity exists by ID.
     *
     * @param id The entity ID
     * @return true if the entity exists
     */
    boolean existsById(ID id);

    /**
     * Counts all entities.
     *
     * @return The total count
     */
    long count();
}
