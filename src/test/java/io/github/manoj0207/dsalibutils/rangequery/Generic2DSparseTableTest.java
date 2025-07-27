package io.github.manoj0207.dsalibutils.rangequery;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class Generic2DSparseTableTest {

    @Test
    void testMinQueryBasic() {
        Integer[][] input = {
                {5, 2, 4},
                {1, 7, 3},
                {9, 6, 8}
        };
        BinaryOperator<Integer> min = Integer::min;

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, min);

        assertEquals(1, table.query(0, 0, 2, 2));
        assertEquals(2, table.query(0, 1, 0, 2));
        assertEquals(3, table.query(1, 2, 2, 2));
        assertEquals(6, table.query(2, 1, 2, 1));
    }

    @Test
    void testMaxQueryBasic() {
        Integer[][] input = {
                {1, 4, 2},
                {3, 9, 7},
                {6, 5, 8}
        };
        BinaryOperator<Integer> max = Integer::max;

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, max);

        assertEquals(9, table.query(0, 0, 2, 2));
        assertEquals(4, table.query(0, 1, 0, 2));
        assertEquals(8, table.query(1, 2, 2, 2));
        assertEquals(5, table.query(2, 1, 2, 1));
    }

    @Test
    void testSingleElementQuery() {
        Integer[][] input = {
                {7, 8},
                {9, 10}
        };

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertEquals(7, table.query(0, 0, 0, 0));
        assertEquals(8, table.query(0, 1, 0, 1));
        assertEquals(9, table.query(1, 0, 1, 0));
        assertEquals(10, table.query(1, 1, 1, 1));
    }

    @Test
    void testSingleRowOrColumn() {
        Integer[][] input = {
                {3, 1, 4, 5}
        };

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertEquals(1, table.query(0, 0, 0, 3));
        assertEquals(4, table.query(0, 2, 0, 2));
        assertEquals(5, table.query(0, 3, 0, 3));
    }

    @Test
    void testNonSquareMatrix() {
        Integer[][] input = {
                {5, 1, 7},
                {6, 2, 3}
        };

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertEquals(1, table.query(0, 0, 1, 2));
        assertEquals(2, table.query(1, 1, 1, 2));
        assertEquals(5, table.query(0, 0, 0, 0));
    }

    @Test
    void testLargeMatrixMin() {
        Integer[][] input = new Integer[8][8];
        for (int i = 0, val = 1; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                input[i][j] = val++;
            }
        }

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertEquals(1, table.query(0, 0, 7, 7));
        assertEquals(2, table.query(0, 1, 0, 1));
        assertEquals(8, table.query(0, 7, 0, 7));
        assertEquals(64, table.query(7, 7, 7, 7));
    }

    @Test
    void testGCDQuery() {
        Integer[][] input = {
                {6, 12, 18},
                {24, 36, 48}
        };

        BinaryOperator<Integer> gcd = (a, b) -> {
            while (b != 0) {
                int temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        };

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, gcd);

        assertEquals(6, table.query(0, 0, 1, 2));
        assertEquals(12, table.query(0, 1, 0, 1));
        assertEquals(24, table.query(1, 0, 1, 0));
    }

    @Test
    void testFromListOfLists() {
        List<List<Integer>> input = List.of(
                List.of(4, 3),
                List.of(2, 1)
        );

        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertEquals(1, table.query(0, 0, 1, 1));
        assertEquals(2, table.query(1, 0, 1, 0));
        assertEquals(3, table.query(0, 1, 0, 1));
    }

    @Test
    void testInvalidQueryBounds() {
        Integer[][] input = {
                {1, 2},
                {3, 4}
        };
        Generic2DSparseTable<Integer> table = new Generic2DSparseTable<>(input, Integer::min);

        assertThrows(IndexOutOfBoundsException.class, () -> table.query(-1, 0, 1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> table.query(0, 0, 2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> table.query(1, 1, 0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> table.query(0, 2, 1, 2));
    }

    @Test
    void testInvalidConstruction() {
        assertThrows(IllegalArgumentException.class, () -> new Generic2DSparseTable<>(new Integer[][]{}, Integer::min));
        assertThrows(IllegalArgumentException.class, () -> new Generic2DSparseTable<>((Integer[][]) null, Integer::min));
        assertThrows(IllegalArgumentException.class, () -> new Generic2DSparseTable<>(new Integer[][]{{1}, null}, Integer::min));
        assertThrows(IllegalArgumentException.class, () -> new Generic2DSparseTable<>(new Integer[][]{{1}, {2, 3}}, Integer::min));
        assertThrows(IllegalArgumentException.class, () -> new Generic2DSparseTable<>(new Integer[][]{{1, 2}}, null));
    }
}
