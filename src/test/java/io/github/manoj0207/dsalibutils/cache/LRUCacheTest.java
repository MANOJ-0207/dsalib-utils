package io.github.manoj0207.dsalibutils.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LRUCache}.
 */
class LRUCacheTest {

    private LRUCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        // Create an LRU cache with capacity 3 for testing
        cache = new LRUCache<>(3);
    }

    @Test
    void testBasicPutAndGet() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
    }

    @Test
    void testLRUEvictionOrder() {
        cache.put("a", 1); // LRU: a
        cache.put("b", 2); // LRU: a -> b
        cache.put("c", 3); // LRU: a -> b -> c

        cache.get("a");    // LRU: b -> c -> a (a becomes most recently used)
        cache.put("d", 4); // LRU: c -> a -> d (evicts b)

        assertNull(cache.get("b")); // b should be evicted
        assertEquals(1, cache.get("a"));
        assertEquals(3, cache.get("c"));
        assertEquals(4, cache.get("d"));
    }

    @Test
    void testUpdateValueMovesToFront() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        cache.put("b", 99);  // Should update value and move 'b' to front
        cache.put("d", 4);   // Should evict 'a' (least recently used)

        assertNull(cache.get("a"));
        assertEquals(99, cache.get("b")); // 'b' should still be in cache
    }

    @Test
    void testEvictionWhenCapacityIsReached() {
        cache.put("x", 1);
        cache.put("y", 2);
        cache.put("z", 3);

        cache.put("w", 4); // should evict x

        assertNull(cache.get("x"));
        assertEquals(2, cache.get("y"));
        assertEquals(3, cache.get("z"));
        assertEquals(4, cache.get("w"));
    }

    @Test
    void testNullKeyPutThrows() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, 5));
    }

    @Test
    void testNullKeyGetThrows() {
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
    }

    @Test
    void testNegativeCapacityThrows() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(-1));
    }

    @Test
    void testOverwriteSameKeyKeepsSizeConstant() {
        cache.put("a", 1);
        cache.put("a", 2);
        cache.put("a", 3);
        cache.put("b", 4);

        // Add fewer than capacity items with duplicates
        cache.put("a", 100);

        cache.put("c", 5);

        // Only 3 unique keys should be present
        assertEquals(3, countNonNullKeys("a", "b", "c", "d"));
        assertEquals(100, cache.get("a")); // Should reflect latest value
    }


    @Test
    void testRepeatedAccessPreventsEviction() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        // Access 'a' multiple times to make it most recently used
        cache.get("a");
        cache.get("a");

        cache.put("d", 4); // Should evict 'b'

        assertNull(cache.get("b"));
        assertNotNull(cache.get("a"));
        assertNotNull(cache.get("c"));
        assertNotNull(cache.get("d"));
    }

    private int countNonNullKeys(String... keys) {
        int count = 0;
        for (String key : keys) {
            if (cache.get(key) != null) {
                count++;
            }
        }
        return count;
    }

}
