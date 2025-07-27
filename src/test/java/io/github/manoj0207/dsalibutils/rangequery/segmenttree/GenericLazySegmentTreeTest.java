package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class GenericLazySegmentTreeTest {

    private GenericLazySegmentTree<Integer> sumTree;

    @BeforeEach
    void setUp() {
        Integer[] arr = {1, 3, 5, 7, 9, 11};
        BinaryOperator<Integer> sum = Integer::sum;
        sumTree = new GenericLazySegmentTree<>(arr, sum, 0);
    }

    @Test
    void testQueryBasic() {
        assertEquals(36, sumTree.query(0, 5));
        assertEquals(9, sumTree.query(0, 2));
        assertEquals(27, sumTree.query(3, 5));
        assertEquals(11, sumTree.query(5, 5));
    }

    @Test
    void testUpdatePointWithValue() {
        sumTree.update(2, 100);  // index 2 = 5 → 100
        assertEquals(131, sumTree.query(0, 5));
        assertEquals(116, sumTree.query(2, 4));
    }

    @Test
    void testUpdatePointWithFunction() {
        sumTree.update(1, x -> x * 10); // 3 → 30
        assertEquals(63, sumTree.query(0, 5));
        assertEquals(30, sumTree.query(1, 1));
    }

    @Test
    void testUpdateRangeFunction() {
        sumTree.update(1, 3, x -> x + 2); // add 2 to indices 1,2,3
        // New: [1, 5, 7, 9, 9, 11]
        assertEquals(42, sumTree.query(0, 5));
        assertEquals(21, sumTree.query(1, 3));
    }

    @Test
    void testMultipleLazyRangeUpdates() {
        sumTree.update(0, 2, x -> x + 1);  // +1 to [0,1,2]
        sumTree.update(2, 5, x -> x * 2);  // *2 to [2,3,4,5]
        // Expected: [2,4,(5+1)*2=12,14,18,22]
        assertEquals(71, sumTree.query(0, 5));
        assertEquals(26, sumTree.query(2, 3));
        assertEquals(5, sumTree.query(0, 1));
    }

    @Test
    void testSize() {
        assertEquals(6, sumTree.size());
    }

    @Test
    void testSingleElementTree() {
        GenericLazySegmentTree<Integer> single = new GenericLazySegmentTree<>(
                new Integer[]{42}, Integer::sum, 0);
        assertEquals(42, single.query(0, 0));
        single.update(0, x -> x + 10);
        assertEquals(52, single.query(0, 0));
    }

    @Test
    void testConstructorWithList() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        GenericLazySegmentTree<Integer> tree = new GenericLazySegmentTree<>(list, Integer::sum, 0);
        assertEquals(6, tree.query(0, 2));
    }

    @Test
    void testConstructorWithEmptyInput() {
        assertThrows(IllegalArgumentException.class, () ->
                new GenericLazySegmentTree<>(new Integer[0], Integer::sum, 0));
    }

    @Test
    void testInvalidPointUpdate() {
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(6, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(-1, x -> x + 1));
        assertThrows(IndexOutOfBoundsException.class, () -> sumTree.update(6, x -> x + 1));
        assertThrows(IllegalArgumentException.class, () -> sumTree.update(2, (Function)null));
    }

    @Test
    void testInvalidRangeUpdate() {
        assertThrows(IllegalArgumentException.class, () -> sumTree.update(3, 2, x -> x + 1));
        assertThrows(IllegalArgumentException.class, () -> sumTree.update(-1, 2, x -> x + 1));
        assertThrows(IllegalArgumentException.class, () -> sumTree.update(1, 6, x -> x + 1));
        assertThrows(IllegalArgumentException.class, () -> sumTree.update(1, 3, null));
    }

    @Test
    void testInvalidQuery() {
        assertThrows(IllegalArgumentException.class, () -> sumTree.query(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> sumTree.query(3, 2));
        assertThrows(IllegalArgumentException.class, () -> sumTree.query(0, 6));
    }
}
