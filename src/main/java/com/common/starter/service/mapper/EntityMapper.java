package com.common.starter.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic interface for entity-DTO mapping.
 * Works well with MapStruct for automatic implementation.
 *
 * @param <E> The entity type
 * @param <D> The DTO type
 */
public interface EntityMapper<E, D> {

    /**
     * Converts an entity to a DTO.
     *
     * @param entity The entity to convert
     * @return The DTO
     */
    D toDto(E entity);

    /**
     * Converts a DTO to an entity.
     *
     * @param dto The DTO to convert
     * @return The entity
     */
    E toEntity(D dto);

    /**
     * Converts a list of entities to DTOs.
     *
     * @param entities The entities to convert
     * @return List of DTOs
     */
    default List<D> toDto(List<E> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of DTOs to entities.
     *
     * @param dtos The DTOs to convert
     * @return List of entities
     */
    default List<E> toEntity(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing entity from a DTO.
     * Useful for partial updates.
     *
     * @param dto    The DTO with update data
     * @param entity The entity to update
     */
    void updateEntityFromDto(D dto, E entity);
}
