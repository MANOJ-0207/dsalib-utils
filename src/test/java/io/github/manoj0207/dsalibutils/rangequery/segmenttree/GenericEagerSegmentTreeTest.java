package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class GenericEagerSegmentTreeTest {

    private GenericEagerSegmentTree<Integer> sumTree;
    private GenericEagerSegmentTree<Integer> minTree;

    @BeforeEach
    void setup() {
        Integer[] input = {1, 3, 5, 7, 9, 11};
        sumTree = new GenericEagerSegmentTree<>(input, Integer::sum, 0);
        minTree = new GenericEagerSegmentTree<>(List.of(input), Integer::min, Integer.MAX_VALUE);
    }

    @Test
    void testSize() {
        assertEquals(6, sumTree.size());
        assertEquals(6, minTree.size());
    }

    @Test
    void testQuerySum() {
        assertEquals(36, sumTree.query(0, 5));
        assertEquals(9, sumTree.query(0, 2));
        assertEquals(27, sumTree.query(3, 5));
        assertEquals(9, sumTree.query(4, 4)); // single element
    }

    @Test
    void testQueryMin() {
        assertEquals(1, minTree.query(0, 5));
        assertEquals(3, minTree.query(1, 2));
        assertEquals(7, minTree.query(3, 4));
        assertEquals(11, minTree.query(5, 5));
    }

    @Test
    void testUpdateSumTree() {
        sumTree.update(0, 100); // 1 → 100
        sumTree.update(5, 200); // 11 → 200

        assertEquals(324, sumTree.query(0, 5));
        assertEquals(100, sumTree.query(0, 0));
        assertEquals(200, sumTree.query(5, 5));
    }

    @Test
    void testUpdateMinTree() {
        minTree.update(0, 100); // 1 → 100
        minTree.update(1, 200); // 3 → 200

        assertEquals(5, minTree.query(0, 2)); // Remaining min
        minTree.update(2, 250);
        assertEquals(7, minTree.query(0, 5)); // next min
    }

    @Test
    void testSingleElementTree() {
        var single = new GenericEagerSegmentTree<>(new Integer[]{42}, Integer::sum, 0);
        assertEquals(42, single.query(0, 0));
        assertEquals(1, single.size());

        single.update(0, 100);
        assertEquals(100, single.query(0, 0));
    }

    @Test
    void testAllSameValues() {
        var same = new GenericEagerSegmentTree<>(new Integer[]{5, 5, 5, 5, 5}, Integer::sum, 0);
        assertEquals(25, same.query(0, 4));
        same.update(2, 10);
        assertEquals(30, same.query(0, 4));
    }

    @Test
    void testInvalidRangeThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.query(-1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.query(0, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.query(4, 2));
    }

    @Test
    void testInvalidUpdateThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(-1, 100));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(6, 100));
    }

    @Test
    void testConstructorWithInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new GenericEagerSegmentTree<>(new Integer[0], Integer::sum, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new GenericEagerSegmentTree<>(new Integer[]{1, 2}, null, 0));
    }

    @Test
    void testIdentityIsUsedCorrectly() {
        var maxTree = new GenericEagerSegmentTree<>(new Integer[]{1, 2, 3}, Integer::max, Integer.MIN_VALUE);
        assertEquals(3, maxTree.query(0, 2));
        maxTree.update(1, Integer.MIN_VALUE);
        assertEquals(3, maxTree.query(0, 2));
    }

    @Test
    void testGenericTypeString() {
        var stringTree = new GenericEagerSegmentTree<>(
                new String[]{"a", "ab", "abc"}, (a, b) -> a + b, ""
        );

        assertEquals("aababc", stringTree.query(0, 2));
        stringTree.update(1, "-");
        assertEquals("a-abc", stringTree.query(0, 2));
    }

    @Test
    void testMinLengthStringTree() {
        var minLen = new GenericEagerSegmentTree<>(
                new String[]{"hello", "hi", "hey"},
                (a, b) -> a.length() < b.length() ? a : b,
                "~"
        );

        assertEquals("hi", minLen.query(0, 2));
        minLen.update(1, "greetings");
        assertEquals("hey", minLen.query(0, 2));
    }

    @Test
    void testEdgeUpdates() {
        sumTree.update(0, 1000);
        sumTree.update(5, 2000);
        assertEquals(36 - 1 - 11 + 1000 + 2000, sumTree.query(0, 5));
    }

    @Test
    void testUpdateToIdentity() {
        sumTree.update(2, 0);  // 5 → 0
        assertEquals(31, sumTree.query(0, 5));
    }
}
