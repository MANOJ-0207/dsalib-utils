package io.github.manoj0207.dsalibutils.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FIFOCache}.
 */
class FIFOCacheTest {

    private FIFOCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new FIFOCache<>(3);
    }

    @Test
    void testPutAndGet() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        assertEquals(1, cache.get("a"));
        assertEquals(2, cache.get("b"));
        assertEquals(3, cache.get("c"));
    }

    @Test
    void testUpdateValue() {
        cache.put("x", 10);
        cache.put("x", 20); // update value

        assertEquals(20, cache.get("x"));
        assertEquals(1, cache.size()); // size should not grow
    }

    @Test
    void testFIFOEviction() {
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);

        // At capacity
        assertEquals(3, cache.size());

        cache.put("four", 4); // should evict "one"

        assertFalse(cache.contains("one"));
        assertTrue(cache.contains("two"));
        assertTrue(cache.contains("three"));
        assertTrue(cache.contains("four"));
    }

    @Test
    void testContains() {
        cache.put("z", 100);
        assertTrue(cache.contains("z"));
        assertFalse(cache.contains("y"));
    }

    @Test
    void testClear() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.clear();

        assertEquals(0, cache.size());
        assertFalse(cache.contains("a"));
    }

    @Test
    void testNullKeyInPutThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, 1));
    }

    @Test
    void testNullKeyInGetThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.get(null));
    }

    @Test
    void testNullKeyInContainsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.contains(null));
    }

    @Test
    void testConstructorWithInvalidCapacityThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new FIFOCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new FIFOCache<>(-1));
    }
}
