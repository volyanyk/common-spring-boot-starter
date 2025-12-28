package com.common.starter.persistence.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * Generic interface for cache operations.
 * Provides abstraction over different cache implementations (Redis, Caffeine,
 * etc.).
 *
 * @param <K> The type of cache keys
 * @param <V> The type of cache values
 */
public interface CacheOperations<K, V> {

    /**
     * Retrieves a value from the cache.
     *
     * @param key The cache key
     * @return Optional containing the value if present
     */
    Optional<V> get(K key);

    /**
     * Stores a value in the cache.
     *
     * @param key   The cache key
     * @param value The value to store
     */
    void put(K key, V value);

    /**
     * Stores a value in the cache with expiration.
     *
     * @param key   The cache key
     * @param value The value to store
     * @param ttl   Time to live for the cache entry
     */
    void put(K key, V value, Duration ttl);

    /**
     * Removes a value from the cache.
     *
     * @param key The cache key
     * @return true if the key existed and was removed
     */
    boolean evict(K key);

    /**
     * Removes multiple values from the cache.
     *
     * @param keys The cache keys to remove
     * @return The number of keys that were removed
     */
    long evict(Set<K> keys);

    /**
     * Clears all entries from the cache.
     */
    void clear();

    /**
     * Checks if a key exists in the cache.
     *
     * @param key The cache key
     * @return true if the key exists
     */
    boolean exists(K key);

    /**
     * Gets all keys matching a pattern.
     * Pattern syntax depends on the cache implementation.
     *
     * @param pattern The key pattern
     * @return Set of matching keys
     */
    Set<K> keys(String pattern);

    /**
     * Sets expiration time for an existing key.
     *
     * @param key The cache key
     * @param ttl Time to live
     * @return true if the expiration was set
     */
    boolean expire(K key, Duration ttl);
}
