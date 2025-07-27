package io.github.manoj0207.dsalibutils.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LFUCache}.
 */
class LFUCacheTest {

    private LFUCache<String, Integer> cache;

    @BeforeEach
    void setup() {
        cache = new LFUCache<>(3);
    }

    @Test
    void testPutAndGetBasic() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
    }

    @Test
    void testEvictionLeastFrequentlyUsed() {
        cache.put("a", 1); // freq = 1
        cache.put("b", 2); // freq = 1
        cache.put("c", 3); // freq = 1

        cache.get("a"); // freq = 2
        cache.get("b"); // freq = 2

        // All keys now in cache. Next put should evict key with freq = 1, which is "c"
        cache.put("d", 4);

        assertNull(cache.get("c"));
        assertNotNull(cache.get("a"));
        assertNotNull(cache.get("b"));
        assertNotNull(cache.get("d"));
    }

    @Test
    void testEvictionWithTieBreakerFIFO() {
        cache.put("x", 10);
        cache.put("y", 20);
        cache.put("z", 30);

        // All same frequency (1), add a new one
        cache.put("w", 40);

        // "x" is oldest and least frequently used
        assertNull(cache.get("x"));
        assertEquals(20, cache.get("y"));
        assertEquals(30, cache.get("z"));
        assertEquals(40, cache.get("w"));
    }

    @Test
    void testUpdateValueAndFrequencyIncrease() {
        cache.put("k", 100);
        assertEquals(100, cache.get("k"));

        cache.put("k", 200); // should update value but preserve/increase frequency
        assertEquals(200, cache.get("k"));

        // Add more and test eviction after increasing "k"'s frequency
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3); // should evict least freq, not "k"

        assertTrue(cache.contains("k"));
    }

    @Test
    void testRemoveKey() {
        cache.put("a", 1);
        cache.put("b", 2);
        assertTrue(cache.contains("a"));

        cache.remove("a");
        assertFalse(cache.contains("a"));
        assertNull(cache.get("a"));
    }

    @Test
    void testClear() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.clear();

        assertEquals(0, cache.size());
        assertFalse(cache.contains("a"));
        assertFalse(cache.contains("b"));
    }

    @Test
    void testSizeTracking() {
        assertEquals(0, cache.size());
        cache.put("a", 10);
        cache.put("b", 20);
        assertEquals(2, cache.size());
    }

    @Test
    void testConstructorWithInvalidCapacityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new LFUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LFUCache<>(-1));
    }

    @Test
    void testNullKeyPutThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, 123));
    }

    @Test
    void testNullKeyGetThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
    }

    @Test
    void testNullKeyRemoveThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.remove(null));
    }

    @Test
    void testNullKeyContainsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.contains(null));
    }
}
