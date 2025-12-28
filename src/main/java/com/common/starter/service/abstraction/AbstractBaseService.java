package com.common.starter.service.abstraction;

import com.common.starter.persistence.entity.BaseEntity;
import com.common.starter.persistence.repository.BaseRepository;
import com.common.starter.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base implementation of BaseService.
 * Provides common CRUD operations using a repository.
 * Subclasses can override methods to add custom behavior.
 *
 * @param <T>  The entity type
 * @param <ID> The entity's identifier type
 * @param <R>  The repository type
 */
@Transactional(readOnly = true)
public abstract class AbstractBaseService<T extends BaseEntity<ID>, ID extends Serializable, R extends BaseRepository<T, ID>>
        implements BaseService<T, ID> {

    protected final R repository;

    protected AbstractBaseService(R repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T create(T entity) {
        beforeCreate(entity);
        validate(entity);
        T saved = repository.save(entity);
        afterCreate(saved);
        return saved;
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        T existing = findByIdOrThrow(id);
        beforeUpdate(existing, entity);
        validate(entity);
        entity.setId(id);
        T updated = repository.save(entity);
        afterUpdate(updated);
        return updated;
    }

    @Override
    @Transactional
    public T partialUpdate(ID id, T partialEntity) {
        T existing = findByIdOrThrow(id);
        beforePartialUpdate(existing, partialEntity);
        T merged = mergeForUpdate(existing, partialEntity);
        validate(merged);
        T updated = repository.save(merged);
        afterUpdate(updated);
        return updated;
    }

    @Override
    @Transactional
    public void delete(ID id) {
        T entity = findByIdOrThrow(id);
        beforeDelete(entity);
        repository.delete(entity);
        afterDelete(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T findByIdOrThrow(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getEntityName() + " not found with id: " + id));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    /**
     * Hook method called before entity creation.
     * Override to add custom pre-create logic.
     *
     * @param entity The entity to be created
     */
    protected void beforeCreate(T entity) {
        // Default: do nothing
    }

    /**
     * Hook method called after entity creation.
     * Override to add custom post-create logic.
     *
     * @param entity The created entity
     */
    protected void afterCreate(T entity) {
        // Default: do nothing
    }

    /**
     * Hook method called before entity update.
     * Override to add custom pre-update logic.
     *
     * @param existing The existing entity
     * @param updated  The updated entity data
     */
    protected void beforeUpdate(T existing, T updated) {
        // Default: do nothing
    }

    /**
     * Hook method called before partial update.
     * Override to add custom pre-update logic.
     *
     * @param existing The existing entity
     * @param partial  The partial update data
     */
    protected void beforePartialUpdate(T existing, T partial) {
        // Default: do nothing
    }

    /**
     * Hook method called after entity update.
     * Override to add custom post-update logic.
     *
     * @param entity The updated entity
     */
    protected void afterUpdate(T entity) {
        // Default: do nothing
    }

    /**
     * Hook method called before entity deletion.
     * Override to add custom pre-delete logic.
     *
     * @param entity The entity to be deleted
     */
    protected void beforeDelete(T entity) {
        // Default: do nothing
    }

    /**
     * Hook method called after entity deletion.
     * Override to add custom post-delete logic.
     *
     * @param entity The deleted entity
     */
    protected void afterDelete(T entity) {
        // Default: do nothing
    }

    /**
     * Validates an entity before persistence.
     * Override to add custom validation logic.
     *
     * @param entity The entity to validate
     * @throws com.common.starter.service.exception.ValidationException if
     *                                                                  validation
     *                                                                  fails
     */
    protected void validate(T entity) {
        // Default: do nothing (rely on Bean Validation)
    }

    /**
     * Merges partial update data into existing entity.
     * Must be implemented by subclasses for partial update support.
     *
     * @param existing The existing entity
     * @param partial  The partial update data
     * @return The merged entity
     */
    protected abstract T mergeForUpdate(T existing, T partial);

    /**
     * Gets the entity name for error messages.
     * Override to provide a custom name.
     *
     * @return The entity name
     */
    protected String getEntityName() {
        return "Entity";
    }
}
