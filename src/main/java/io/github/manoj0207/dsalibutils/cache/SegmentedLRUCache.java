package io.github.manoj0207.dsalibutils.cache;

import java.util.*;

/**
 * A Segmented LRU (SLRU) cache that splits the cache into two segments:
 * - A "recently used" segment where items enter on first put
 * - A "frequently used" segment where items get promoted on second access
 *
 * This structure helps prioritize items that are truly frequently accessed
 * over items that were added recently but never used.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class SegmentedLRUCache<K, V>{

    private final int recentCapacity;
    private final int frequentCapacity;

    private final LinkedHashMap<K, V> recentlyUsedSegment;
    private final LinkedHashMap<K, V> frequentlyUsedSegment;

    /**
     * Constructs a Segmented LRU cache.
     *
     * @param totalCapacity the total maximum number of entries in the cache
     * @param frequentSegmentRatio the fraction of capacity allocated to the frequent segment (between 0 and 1)
     * @throws IllegalArgumentException if totalCapacity <= 0 or ratio not in (0, 1)
     */
    public SegmentedLRUCache(int totalCapacity, double frequentSegmentRatio) {
        if (totalCapacity <= 0) {
            throw new IllegalArgumentException("Total capacity must be positive.");
        }
        if (frequentSegmentRatio <= 0 || frequentSegmentRatio >= 1) {
            throw new IllegalArgumentException("Frequent segment ratio must be > 0 and < 1.");
        }

        this.frequentCapacity = (int) (totalCapacity * frequentSegmentRatio);
        this.recentCapacity = totalCapacity - frequentCapacity;

        this.recentlyUsedSegment = new LinkedHashMap<>(recentCapacity, 0.75f, true);
        this.frequentlyUsedSegment = new LinkedHashMap<>(frequentCapacity, 0.75f, true);
    }

    /**
     * Retrieves the value for a given key if present.
     * If present in the recently used segment, promotes it to the frequently used segment.
     *
     * @param key the key to retrieve
     * @return the value if present, otherwise null
     */
    public V get(K key) {
        if (frequentlyUsedSegment.containsKey(key)) {
            return frequentlyUsedSegment.get(key);
        }

        if (recentlyUsedSegment.containsKey(key)) {
            V value = recentlyUsedSegment.remove(key);
            promoteToFrequent(key, value);
            return value;
        }

        return null;
    }

    /**
     * Inserts a key-value pair into the cache.
     * If key already exists in any segment, it updates the value.
     * Otherwise, inserts into the recently used segment and evicts if necessary.
     *
     * @param key the key to insert
     * @param value the value to insert
     */
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");
        if (value == null) throw new IllegalArgumentException("Value cannot be null.");

        if (frequentlyUsedSegment.containsKey(key)) {
            frequentlyUsedSegment.put(key, value);
            return;
        }

        if (recentlyUsedSegment.containsKey(key)) {
            recentlyUsedSegment.put(key, value);
            return;
        }

        if (recentlyUsedSegment.size() >= recentCapacity) {
            evictOldest(recentlyUsedSegment);
        }

        recentlyUsedSegment.put(key, value);
    }

    /**
     * Promotes a key-value pair to the frequently used segment.
     * Evicts from the frequent segment if over capacity.
     *
     * @param key the key to promote
     * @param value the value associated
     */
    private void promoteToFrequent(K key, V value) {
        if (frequentlyUsedSegment.size() >= frequentCapacity) {
            evictOldest(frequentlyUsedSegment);
        }
        frequentlyUsedSegment.put(key, value);
    }

    /**
     * Evicts the oldest entry from a given LRU-based segment.
     *
     * @param map the map segment to evict from
     */
    private void evictOldest(LinkedHashMap<K, V> map) {
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    /**
     * Returns the number of items currently in the cache.
     */
    public int size() {
        return recentlyUsedSegment.size() + frequentlyUsedSegment.size();
    }

    /**
     * Checks if a key exists in the cache (in either segment).
     */
    public boolean contains(K key) {
        return recentlyUsedSegment.containsKey(key) || frequentlyUsedSegment.containsKey(key);
    }

    /**
     * Removes a key from the cache if present.
     */
    public void remove(K key) {
        recentlyUsedSegment.remove(key);
        frequentlyUsedSegment.remove(key);
    }

    /**
     * Clears all keys from both segments.
     */
    public void clear() {
        recentlyUsedSegment.clear();
        frequentlyUsedSegment.clear();
    }
}
