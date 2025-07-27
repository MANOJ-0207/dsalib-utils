package io.github.manoj0207.dsalibutils.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SegmentedLRUCache}.
 * Covers construction, insertion, promotion, eviction, retrieval, removal, and clearing.
 */
class SegmentedLRUCacheTest {

    private SegmentedLRUCache<String, String> cache;

    @BeforeEach
    void setup() {
        // total capacity = 4, 50% allocated to frequent segment
        cache = new SegmentedLRUCache<>(4, 0.5);
    }

    @Test
    void testConstructor_invalidCapacity_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SegmentedLRUCache<>(0, 0.5));
        assertThrows(IllegalArgumentException.class, () -> new SegmentedLRUCache<>(3, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new SegmentedLRUCache<>(3, 1.0));
    }

    @Test
    void testPutAndGet_basicFunctionality() {
        cache.put("a", "apple");
        cache.put("b", "banana");

        assertEquals("apple", cache.get("a"));
        assertEquals("banana", cache.get("b"));
    }

    @Test
    void testGet_promotesToFrequentSegment() {
        cache.put("x", "one");
        assertNull(cache.get("y")); // non-existent

        // First access – should promote to frequent
        assertEquals("one", cache.get("x"));

        // Still available from frequent
        assertEquals("one", cache.get("x"));
    }

    @Test
    void testEvictionFromRecentlyUsedSegment() {
        cache.put("a", "a");
        cache.put("b", "b");
        cache.put("c", "c"); // Evicts oldest ("a") since recent capacity = 2

        assertNull(cache.get("a"));
        assertEquals("b", cache.get("b"));
        assertEquals("c", cache.get("c"));
    }

    @Test
    void testEvictionFromFrequentlyUsedSegment() {
        cache.put("a", "1");
        cache.put("b", "2");

        // Promote both to frequent
        cache.get("a");
        cache.get("b");

        // Add new, triggers recent segment eviction
        cache.put("c", "3");
        cache.get("c"); // promote to frequent, now frequent is full

        // Adding and promoting a 4th entry to frequent causes eviction
        cache.put("d", "4");
        cache.get("d");

        // At this point, either "a" or "b" should have been evicted from frequent segment
        int existing = 0;
        for (String key : new String[]{"a", "b", "c", "d"}) {
            if (cache.contains(key)) existing++;
        }
        assertEquals(2, existing); // all items still tracked in either segment
    }

    @Test
    void testUpdateValue_existingKey() {
        cache.put("x", "100");
        assertEquals("100", cache.get("x"));

        cache.put("x", "200"); // update in frequent
        assertEquals("200", cache.get("x"));
    }

    @Test
    void testRemoveKey() {
        cache.put("a", "x");
        cache.get("a"); // promote to frequent
        assertTrue(cache.contains("a"));

        cache.remove("a");
        assertFalse(cache.contains("a"));
        assertNull(cache.get("a"));
    }

    @Test
    void testClear() {
        cache.put("x", "x");
        cache.put("y", "y");
        cache.put("z", "z");

        assertTrue(cache.size() > 0);

        cache.clear();
        assertEquals(0, cache.size());
        assertFalse(cache.contains("x"));
        assertFalse(cache.contains("y"));
        assertFalse(cache.contains("z"));
    }

    @Test
    void testPut_nullKeyOrValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> cache.put("key", null));
    }

    @Test
    void testSizeTracking() {
        cache.put("a", "1");
        cache.put("b", "2");
        assertEquals(2, cache.size());

        cache.get("a"); // promote to frequent
        assertEquals(2, cache.size());

        cache.put("c", "3"); // goes to recent
        assertEquals(3, cache.size());
    }
}
