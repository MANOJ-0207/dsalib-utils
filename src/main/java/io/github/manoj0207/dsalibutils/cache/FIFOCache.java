package io.github.manoj0207.dsalibutils.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A fixed-capacity FIFO (First-In-First-Out) cache implementation.
 *
 * <p>When the cache exceeds its capacity, the oldest inserted entry is evicted.</p>
 *
 * @param <K> the type of keys used in this cache
 * @param <V> the type of values stored in this cache
 */
public class FIFOCache<K, V> {

    /** Maximum number of entries the cache can hold */
    private final int capacity;

    /** Internal map maintaining insertion order */
    private final Map<K, V> map;

    /**
     * Constructs a FIFO cache with the specified capacity.
     *
     * @param capacity the maximum number of elements in the cache
     * @throws IllegalArgumentException if capacity is not positive
     */
    public FIFOCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be positive.");
        }
        this.capacity = capacity;
        this.map = new LinkedHashMap<>();
    }

    /**
     * Retrieves the value associated with the given key.
     *
     * @param key the key to look up
     * @return the associated value, or null if not found
     * @throws IllegalArgumentException if the key is null
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return map.getOrDefault(key, null);
    }

    /**
     * Inserts or updates the value for the given key.
     * If the key already exists, its value is updated.
     * If the cache exceeds capacity, the oldest entry is evicted.
     *
     * @param key   the key to insert or update
     * @param value the value to associate with the key
     * @throws IllegalArgumentException if the key is null
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (map.containsKey(key)) {
            map.put(key, value);
            return;
        }

        if (map.size() >= capacity) {
            K oldest = map.keySet().iterator().next(); // FIFO eviction
            map.remove(oldest);
        }

        map.put(key, value);
    }

    /**
     * Returns the current size of the cache.
     *
     * @return number of entries in the cache
     */
    public int size() {
        return map.size();
    }

    /**
     * Clears all entries in the cache.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Checks whether the cache contains the given key.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     * @throws IllegalArgumentException if the key is null
     */
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return map.containsKey(key);
    }
}
