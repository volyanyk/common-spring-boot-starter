package com.common.starter.web.controller;

import com.common.starter.service.abstraction.BaseService;
import com.common.starter.web.response.ApiResponse;
import com.common.starter.web.response.PagedResponse;
import com.common.starter.web.response.RestResponseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * Abstract base controller providing common CRUD endpoints.
 * Subclasses can extend this to get standard REST operations.
 *
 * @param <T>  The entity/DTO type
 * @param <ID> The entity's identifier type
 * @param <S>  The service type
 */
public abstract class BaseController<T, ID extends Serializable, S extends BaseService<T, ID>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    /**
     * Creates a new resource.
     *
     * @param entity The entity to create
     * @return ResponseEntity with created resource
     */
    @PostMapping
    public ResponseEntity<ApiResponse<T>> create(@Valid @RequestBody T entity) {
        T created = service.create(entity);
        return RestResponseBuilder.created(created);
    }

    /**
     * Updates an existing resource.
     *
     * @param id     The resource ID
     * @param entity The updated entity data
     * @return ResponseEntity with updated resource
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @Valid @RequestBody T entity) {
        T updated = service.update(id, entity);
        return RestResponseBuilder.success(updated, "Resource updated successfully");
    }

    /**
     * Partially updates an existing resource.
     *
     * @param id            The resource ID
     * @param partialEntity The partial update data
     * @return ResponseEntity with updated resource
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> partialUpdate(@PathVariable ID id, @RequestBody T partialEntity) {
        T updated = service.partialUpdate(id, partialEntity);
        return RestResponseBuilder.success(updated, "Resource updated successfully");
    }

    /**
     * Deletes a resource.
     *
     * @param id The resource ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.delete(id);
        return RestResponseBuilder.noContent();
    }

    /**
     * Gets a resource by ID.
     *
     * @param id The resource ID
     * @return ResponseEntity with the resource
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable ID id) {
        T entity = service.findByIdOrThrow(id);
        return RestResponseBuilder.success(entity);
    }

    /**
     * Gets all resources with pagination.
     *
     * @param pageable Pagination parameters
     * @return ResponseEntity with paginated resources
     */
    @GetMapping
    public ResponseEntity<PagedResponse<T>> getAll(Pageable pageable) {
        Page<T> page = service.findAll(pageable);
        return ResponseEntity.ok(new PagedResponse<>(page));
    }

    /**
     * Gets all resources without pagination.
     *
     * @return ResponseEntity with all resources
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<T>>> getAllUnpaged() {
        List<T> entities = service.findAll();
        return RestResponseBuilder.success(entities);
    }
}
