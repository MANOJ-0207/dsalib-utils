package io.github.manoj0207.dsalibutils.cache;

import java.util.*;

/**
 * A Segmented LRU (SLRU) cache that splits the cache into two segments:
 * <ul>
 *   <li><b>Recently Used:</b> New items are inserted here on first put</li>
 *   <li><b>Frequently Used:</b> Items are promoted here upon second access</li>
 * </ul>
 *
 * <p>This structure helps prioritize items that are frequently accessed
 * over those that were only recently added.</p>
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
public class SegmentedLRUCache<K, V> {

    /**
     * Capacity of the recently used segment.
     */
    private final int recentCapacity;

    /**
     * Capacity of the frequently used segment.
     */
    private final int frequentCapacity;

    /**
     * Segment for recently used items.
     */
    private final LinkedHashMap<K, V> recentlyUsedSegment;

    /**
     * Segment for frequently used items.
     */
    private final LinkedHashMap<K, V> frequentlyUsedSegment;

    /**
     * Constructs a Segmented LRU cache.
     *
     * @param totalCapacity         the total maximum number of entries in the cache
     * @param frequentSegmentRatio the fraction of capacity allocated to the frequent segment (between 0 and 1)
     * @throws IllegalArgumentException if totalCapacity &le; 0 or ratio not in (0, 1)
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
     * Promotes it to the frequently used segment if found in recently used.
     *
     * @param key the key to retrieve
     * @return the value if present, otherwise {@code null}
     *
     * <p><b>Time Complexity:</b> O(1)</p>
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
     * Updates the value if the key already exists in any segment.
     * Evicts from recently used segment if necessary.
     *
     * @param key   the key to insert
     * @param value the value to insert
     * @throws IllegalArgumentException if key or value is {@code null}
     *
     * <p><b>Time Complexity:</b> O(1)</p>
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
     * Promotes a key-value pair from recently used to frequently used segment.
     * Evicts the least recently used item in frequent segment if full.
     *
     * @param key   the key to promote
     * @param value the value associated with the key
     *
     * <p><b>Time Complexity:</b> O(1)</p>
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
     *
     * <p><b>Time Complexity:</b> O(1)</p>
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
     *
     * @return total size across both segments
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public int size() {
        return recentlyUsedSegment.size() + frequentlyUsedSegment.size();
    }

    /**
     * Checks if a key exists in either segment of the cache.
     *
     * @param key the key to check
     * @return {@code true} if key exists, {@code false} otherwise
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public boolean contains(K key) {
        return recentlyUsedSegment.containsKey(key) || frequentlyUsedSegment.containsKey(key);
    }

    /**
     * Removes a key from both segments if it exists.
     *
     * @param key the key to remove
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public void remove(K key) {
        recentlyUsedSegment.remove(key);
        frequentlyUsedSegment.remove(key);
    }

    /**
     * Clears all entries from both segments.
     *
     * <p><b>Time Complexity:</b> O(n)</p>
     */
    public void clear() {
        recentlyUsedSegment.clear();
        frequentlyUsedSegment.clear();
    }
}
