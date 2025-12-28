package com.common.starter.persistence.repository;

import com.common.starter.persistence.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;

/**
 * Base repository interface for all JPA repositories.
 * Extends JpaRepository and JpaSpecificationExecutor for full query
 * capabilities.
 * 
 * @param <T>  The entity type
 * @param <ID> The entity's identifier type
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID extends Serializable>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Finds an entity by ID or throws an exception if not found.
     * This is a convenience method that avoids Optional handling.
     *
     * @param id The entity ID
     * @return The entity
     * @throws jakarta.persistence.EntityNotFoundException if entity not found
     */
    default T findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Entity not found with id: " + id));
    }

    /**
     * Checks if an entity exists by ID or throws an exception if not found.
     *
     * @param id The entity ID
     * @throws jakarta.persistence.EntityNotFoundException if entity not found
     */
    default void existsByIdOrThrow(ID id) {
        if (!existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Entity not found with id: " + id);
        }
    }

    /**
     * Soft delete support - finds only non-deleted entities.
     * Override this in repositories that support soft delete.
     *
     * @param id The entity ID
     * @return Optional containing the entity if found and not deleted
     */
    default Optional<T> findByIdAndNotDeleted(ID id) {
        return findById(id);
    }
}
