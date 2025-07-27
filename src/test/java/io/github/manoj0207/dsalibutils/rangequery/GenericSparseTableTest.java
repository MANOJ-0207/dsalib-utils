package io.github.manoj0207.dsalibutils.rangequery;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class GenericSparseTableTest {

    @Test
    void testMinQueryOverlapSafe() {
        Integer[] input = {4, 6, 1, 5, 7, 3, 2};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::min, true);

        assertEquals(1, table.query(0, 4));
        assertEquals(2, table.query(5, 6));
        assertEquals(1, table.query(2, 2)); // single element
        assertEquals(3, table.query(4, 5));
    }

    @Test
    void testMaxQueryOverlapSafe() {
        Integer[] input = {1, 9, 3, 8, 4, 5, 6};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::max, true);

        assertEquals(9, table.query(0, 3));
        assertEquals(8, table.query(2, 3));
        assertEquals(5, table.query(4, 5));
        assertEquals(6, table.query(6, 6)); // last element
    }

    @Test
    void testSumQueryNotOverlapSafe() {
        Integer[] input = {1, 2, 3, 4, 5};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::sum, false);

        assertEquals(15, table.query(0, 4));
        assertEquals(9, table.query(1, 3));
        assertEquals(3, table.query(0, 1));
        assertEquals(4, table.query(3, 3)); // single element
    }

    @Test
    void testNonPowerOfTwoSizeInput() {
        Integer[] input = {10, 20, 30, 40, 50, 60}; // length = 6
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::sum, false);

        assertEquals(210, table.query(0, 5));
        assertEquals(90, table.query(1, 3));
    }

    @Test
    void testBuildFromList() {
        List<Integer> input = List.of(1, 3, 5, 7, 9);
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::min, true);

        assertEquals(1, table.query(0, 4));
        assertEquals(3, table.query(1, 2));
        assertEquals(7, table.query(3, 3)); // single element
    }

    @Test
    void testTableStructureAndSize() {
        Integer[] input = {1, 2, 3, 4};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::sum, false);

        List<List<Integer>> internal = table.getTableAsLists();

        assertNotNull(internal);
        assertFalse(internal.isEmpty());
        assertEquals(4, table.size());

        for (List<Integer> row : internal) {
            assertNotNull(row);
        }
    }

    @Test
    void testInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () ->
                new GenericSparseTable<>(new Integer[]{}, Integer::sum));

        assertThrows(IllegalArgumentException.class, () ->
                new GenericSparseTable<>(null, Integer::sum));

        assertThrows(IllegalArgumentException.class, () ->
                new GenericSparseTable<>(new Integer[]{1, 2, 3}, null));

        assertThrows(IllegalArgumentException.class, () ->
                new GenericSparseTable<>(new Integer[]{null, 1, 2}, Integer::sum));
    }

    @Test
    void testInvalidQueryRange() {
        Integer[] input = {1, 2, 3};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::min, true);

        assertThrows(IllegalArgumentException.class, () -> table.query(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> table.query(0, 3));
        assertThrows(IllegalArgumentException.class, () -> table.query(2, 1)); // l > r
    }

    @Test
    void testIsOverlapSafeFlag() {
        GenericSparseTable<Integer> minTable = new GenericSparseTable<>(new Integer[]{1, 2, 3}, Integer::min, true);
        assertTrue(minTable.isOverlapSafe());

        GenericSparseTable<Integer> sumTable = new GenericSparseTable<>(new Integer[]{1, 2, 3}, Integer::sum, false);
        assertFalse(sumTable.isOverlapSafe());
    }

    @Test
    void testGenericTypesString() {
        String[] input = {"a", "ab", "abc"};
        BinaryOperator<String> concat = String::concat;

        GenericSparseTable<String> table = new GenericSparseTable<>(input, concat, false);

        assertEquals("aababc", table.query(0, 2));
        assertEquals("ababc", table.query(1, 2));
        assertEquals("abc", table.query(2, 2));
    }

    @Test
    void testQueryFullRangeSingleElementArray() {
        GenericSparseTable<Integer> table = new GenericSparseTable<>(new Integer[]{42}, Integer::min, true);
        assertEquals(42, table.query(0, 0));
    }

    @Test
    void testQuerySameRangeMultipleTimes() {
        Integer[] input = {1, 3, 5, 7};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::max, true);

        for (int i = 0; i < input.length; i++) {
            assertEquals(input[i], table.query(i, i));
        }
    }

    @Test
    void testQueryMidToEndOverlapSafe() {
        Integer[] input = {9, 7, 6, 5, 3};
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::min, true);

        assertEquals(3, table.query(2, 4));
    }

    @Test
    void testFallbackSumCorrectnessWithOddSize() {
        Integer[] input = {2, 4, 6, 8, 10, 12, 14}; // 7 elements
        GenericSparseTable<Integer> table = new GenericSparseTable<>(input, Integer::sum, false);

        assertEquals(56, table.query(0, 6));
        assertEquals(36, table.query(2, 5));
    }
}
