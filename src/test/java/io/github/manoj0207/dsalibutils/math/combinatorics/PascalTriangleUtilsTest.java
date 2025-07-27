package io.github.manoj0207.dsalibutils.math.combinatorics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PascalTriangleUtilsTest {

    @Test
    void testBuildTriangle() {
        List<List<Integer>> triangle = PascalTriangleUtils.buildTriangle(5);

        assertEquals(List.of(1), triangle.get(0));
        assertEquals(List.of(1, 1), triangle.get(1));
        assertEquals(List.of(1, 2, 1), triangle.get(2));
        assertEquals(List.of(1, 3, 3, 1), triangle.get(3));
        assertEquals(List.of(1, 4, 6, 4, 1), triangle.get(4));
    }

    @Test
    void testBuildTriangleInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.buildTriangle(-1));
    }

    @Test
    void testGetNthRow() {
        assertEquals(List.of(1), PascalTriangleUtils.getNthRow(0));
        assertEquals(List.of(1, 1), PascalTriangleUtils.getNthRow(1));
        assertEquals(List.of(1, 2, 1), PascalTriangleUtils.getNthRow(2));
        assertEquals(List.of(1, 3, 3, 1), PascalTriangleUtils.getNthRow(3));
        assertEquals(List.of(1, 4, 6, 4, 1), PascalTriangleUtils.getNthRow(4));
    }

    @Test
    void testGetNthRowOverflowProtection() {
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getNthRow(34)); // > MAX_SAFE_N
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getNthRow(-5));
    }

    @Test
    void testGetValue() {
        assertEquals(1, PascalTriangleUtils.getValue(4, 0));
        assertEquals(4, PascalTriangleUtils.getValue(4, 1));
        assertEquals(6, PascalTriangleUtils.getValue(4, 2));
        assertEquals(4, PascalTriangleUtils.getValue(4, 3));
        assertEquals(1, PascalTriangleUtils.getValue(4, 4));
        assertEquals(0, PascalTriangleUtils.getValue(4, 5));  // out-of-bounds col
        assertEquals(0, PascalTriangleUtils.getValue(4, -1)); // negative col
    }

    @Test
    void testGetValueInvalidRow() {
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getValue(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getValue(34, 0));
    }

    @Test
    void testGetPeak() {
        assertEquals(1, PascalTriangleUtils.getPeak(0));
        assertEquals(1, PascalTriangleUtils.getPeak(1));
        assertEquals(2, PascalTriangleUtils.getPeak(2));
        assertEquals(3, PascalTriangleUtils.getPeak(3));
        assertEquals(6, PascalTriangleUtils.getPeak(4)); // peak at col = 2
    }

    @Test
    void testGetPeakInvalidRow() {
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getPeak(-1));
        assertThrows(IllegalArgumentException.class, () -> PascalTriangleUtils.getPeak(34));
    }
}
