package io.github.manoj0207.dsalibutils.math.combinatorics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactorialUtilsTest {

    private static final int MOD = 1_000_000_007;
    private static final int MAX = 100;
    private final FactorialUtils utils = new FactorialUtils(MOD, MAX);

    @Test
    void testFactorialBaseCases() {
        assertEquals(1, utils.factorial(0));
        assertEquals(1, utils.factorial(1));
        assertEquals(2, utils.factorial(2));
        assertEquals(6, utils.factorial(3));
        assertEquals(24, utils.factorial(4));
    }

    @Test
    void testInverseFactorialCorrectness() {
        int f5 = utils.factorial(5);
        int invF5 = utils.inverseFactorial(5);
        assertEquals(1, (int)(((long)f5 * invF5) % MOD));
    }

    @Test
    void testFactorialInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> utils.factorial(-1));
        assertThrows(IllegalArgumentException.class, () -> utils.factorial(MAX + 1));
    }

    @Test
    void testInverseFactorialInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> utils.inverseFactorial(-1));
        assertThrows(IllegalArgumentException.class, () -> utils.inverseFactorial(MAX + 1));
    }

    @Test
    void testNcrValid() {
        assertEquals(10, utils.nCr(5, 2)); // 5C2 = 10
        assertEquals(1, utils.nCr(5, 0));
        assertEquals(1, utils.nCr(5, 5));
    }

    @Test
    void testNcrInvalid() {
        assertEquals(0, utils.nCr(5, 6)); // r > n
        assertEquals(0, utils.nCr(-1, 2)); // negative n
        assertEquals(0, utils.nCr(5, -1)); // negative r
        assertEquals(0, utils.nCr(MAX + 1, 1)); // n >= MAX
    }

    @Test
    void testNprValid() {
        assertEquals(60, utils.nPr(5, 3)); // 5P3 = 5*4*3 = 60
    }

    @Test
    void testNprInvalid() {
        assertEquals(0, utils.nPr(3, 5)); // r > n
        assertEquals(0, utils.nPr(-1, 1)); // negative n
        assertEquals(0, utils.nPr(3, -1)); // negative r
        assertEquals(0, utils.nPr(MAX+1, 1)); // n >= MAX
    }

    @Test
    void testCatalanNumbers() {
        assertEquals(1, utils.catalan(0)); // C0 = 1
        assertEquals(1, utils.catalan(1)); // C1 = 1
        assertEquals(2, utils.catalan(2)); // C2 = 2
        assertEquals(5, utils.catalan(3)); // C3 = 5
        assertEquals(14, utils.catalan(4)); // C4 = 14
    }

    @Test
    void testCatalanOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> utils.catalan((MAX / 2) + 1));
    }

    @Test
    void testDerangementValues() {
        assertEquals(1, utils.derangement(0));
        assertEquals(0, utils.derangement(1));
        assertEquals(1, utils.derangement(2));
        assertEquals(2, utils.derangement(3));
        assertEquals(9, utils.derangement(4));
        assertEquals(44, utils.derangement(5));
    }

    @Test
    void testDerangementInvalid() {
        assertThrows(IllegalArgumentException.class, () -> utils.derangement(-1));
    }
}
