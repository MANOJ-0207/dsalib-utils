package io.github.manoj0207.dsalibutils.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract base class for cache implementations.
 * Provides basic map-backed storage and common operations such as
 * {@code contains}, {@code remove}, {@code clear}, and {@code size}.
 *
 * Subclasses must implement {@code get} and {@code put} based on
 * the specific caching strategy (e.g., LRU, LFU, FIFO).
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    /** Maximum number of entries this cache can hold */
    protected final int capacity;

    /** Internal key-value map representing the cache store */
    protected final Map<K, V> store;

    /**
     * Constructs an abstract cache with the given capacity.
     *
     * @param capacity the maximum number of entries allowed in the cache
     * @throws IllegalArgumentException if capacity is non-positive
     */
    public AbstractCache(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Cache capacity not be negative.");
        }
        this.capacity = capacity;
        this.store = new HashMap<>();
    }

    /**
     * Checks if the cache contains a mapping for the given key.
     *
     * @param key the non-null key to check
     * @return true if the cache contains the key, false otherwise
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return store.containsKey(key);
    }

    /**
     * Returns the current number of entries in the cache.
     *
     * @return the cache size
     */
    @Override
    public int size() {
        return store.size();
    }

    /**
     * Removes the entry associated with the given key from the cache.
     * If the key does not exist, the call has no effect.
     *
     * @param key the non-null key to remove
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public void remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        store.remove(key);
    }

    /**
     * Removes all entries from the cache.
     */
    @Override
    public void clear() {
        store.clear();
    }
}
