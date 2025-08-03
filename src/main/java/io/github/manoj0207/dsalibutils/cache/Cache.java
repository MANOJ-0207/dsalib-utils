package io.github.manoj0207.dsalibutils.cache;

/**
 * A generic cache interface representing the basic operations for any key-value based caching system.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public interface Cache<K, V> {

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the non-null key whose associated value is to be returned
     * @return the cached value, or {@code null} if not present
     * @throws IllegalArgumentException if the key is {@code null}
     */
    V get(K key);

    /**
     * Associates the specified value with the specified key in the cache.
     * If the key already exists, its value is updated.
     *
     * @param key   the non-null key with which the value is to be associated
     * @param value the value to be cached
     * @throws IllegalArgumentException if the key is {@code null}
     */
    void put(K key, V value);

    /**
     * Checks whether the cache contains a mapping for the specified key.
     *
     * @param key the non-null key whose presence is to be tested
     * @return {@code true} if the key exists in the cache, {@code false} otherwise
     * @throws IllegalArgumentException if the key is {@code null}
     */
    boolean contains(K key);

    /**
     * Removes the mapping for the specified key from the cache if present.
     *
     * @param key the non-null key whose mapping is to be removed
     * @throws IllegalArgumentException if the key is {@code null}
     */
    void remove(K key);

    /**
     * Returns the number of key-value mappings currently stored in the cache.
     *
     * @return the number of entries in the cache
     */
    int size();

    /**
     * Clears all key-value mappings from the cache.
     */
    void clear();
}
