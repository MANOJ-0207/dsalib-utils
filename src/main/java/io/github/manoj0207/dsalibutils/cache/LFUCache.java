package io.github.manoj0207.dsalibutils.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * LFU (Least Frequently Used) Cache implementation.
 *
 * <p>When the cache exceeds its capacity, the least frequently used key is evicted.
 * If multiple keys have the same frequency, the oldest one is evicted (FIFO among equals).</p>
 *
 * @param <K> the type of keys used in this cache
 * @param <V> the type of values stored in this cache
 */
public class LFUCache<K, V> {

    /**
     * Maximum number of elements the cache can hold.
     */
    private final int capacity;

    /**
     * Tracks the minimum frequency among all keys (used during eviction).
     */
    private int minFreq = 0;

    /**
     * Stores key-value pairs.
     */
    private final Map<K, V> values;

    /**
     * Stores key to frequency mapping.
     */
    private final Map<K, Integer> freqs;

    /**
     * Maps frequency to keys with that frequency (preserving insertion order).
     */
    private final Map<Integer, LinkedHashSet<K>> freqList;

    /**
     * Constructs an LFU cache with the specified capacity.
     *
     * @param capacity the maximum number of entries
     * @throws IllegalArgumentException if capacity is negative
     */
    public LFUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity cannot be negative.");
        }
        this.capacity = capacity;
        this.values = new HashMap<>();
        this.freqs = new HashMap<>();
        this.freqList = new HashMap<>();
    }

    /**
     * Retrieves the value for the given key and updates its frequency.
     *
     * @param key the key to retrieve
     * @return the value associated with the key, or {@code null} if key doesn't exist
     * @throws IllegalArgumentException if key is {@code null}
     *
     * <p><b>Time Complexity:</b> O(1) average</p>
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (!values.containsKey(key)) return null;

        int freq = freqs.get(key);
        freqs.put(key, freq + 1);

        LinkedHashSet<K> currentSet = freqList.get(freq);
        currentSet.remove(key);
        if (currentSet.isEmpty()) {
            freqList.remove(freq);
            if (freq == minFreq) minFreq++;
        }

        freqList.computeIfAbsent(freq + 1, f -> new LinkedHashSet<>()).add(key);
        return values.get(key);
    }

    /**
     * Inserts or updates a key-value pair in the cache.
     * If the key exists, updates its value and frequency.
     * If not, adds a new entry and evicts one if the cache is full.
     *
     * @param key   the key to insert or update
     * @param value the value to associate with the key
     * @throws IllegalArgumentException if key is {@code null}
     *
     * <p><b>Time Complexity:</b> O(1) average</p>
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (capacity == 0) return;

        if (values.containsKey(key)) {
            values.put(key, value);
            get(key); // Update frequency via `get` logic
            return;
        }

        if (values.size() >= capacity) {
            LinkedHashSet<K> minFreqSet = freqList.get(minFreq);
            K evict = minFreqSet.iterator().next();
            minFreqSet.remove(evict);
            if (minFreqSet.isEmpty()) {
                freqList.remove(minFreq);
            }
            values.remove(evict);
            freqs.remove(evict);
        }

        values.put(key, value);
        freqs.put(key, 1);
        freqList.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    /**
     * Returns the number of key-value pairs currently stored in the cache.
     *
     * @return current cache size
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public int size() {
        return values.size();
    }

    /**
     * Checks whether the given key exists in the cache.
     *
     * @param key the key to check
     * @return {@code true} if key exists, {@code false} otherwise
     * @throws IllegalArgumentException if key is {@code null}
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
        return values.containsKey(key);
    }

    /**
     * Removes a key from the cache if it exists.
     *
     * @param key the key to remove
     * @throws IllegalArgumentException if key is {@code null}
     *
     * <p><b>Time Complexity:</b> O(1) average</p>
     */
    public void remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (!values.containsKey(key)) return;

        int freq = freqs.get(key);
        LinkedHashSet<K> set = freqList.get(freq);
        if (set != null) {
            set.remove(key);
            if (set.isEmpty()) {
                freqList.remove(freq);
                if (minFreq == freq) {
                    minFreq = findNextMinFreq(freq);
                }
            }
        }

        values.remove(key);
        freqs.remove(key);
    }

    /**
     * Clears all entries from the cache.
     *
     * <p><b>Time Complexity:</b> O(n), where n is the number of entries</p>
     */
    public void clear() {
        values.clear();
        freqs.clear();
        freqList.clear();
        minFreq = 0;
    }

    /**
     * Finds the next lowest frequency present in the frequency map.
     *
     * @param current the current frequency
     * @return the next minimum frequency if found, else 0
     *
     * <p><b>Time Complexity:</b> O(f), where f is the number of distinct frequencies</p>
     */
    private int findNextMinFreq(int current) {
        int next = Integer.MAX_VALUE;
        for (int freq : freqList.keySet()) {
            if (freq > current) {
                next = Math.min(next, freq);
            }
        }
        return next == Integer.MAX_VALUE ? 0 : next;
    }
}
