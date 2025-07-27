package io.github.manoj0207.dsalibutils.math.numbertheory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModMathTest {

    private final int MOD = 1_000_000_007;
    private final ModMath modMath = new ModMath(MOD);

    @Test
    void testModAdd() {
        assertEquals(7, modMath.modAdd(3, 4));
        assertEquals(0, modMath.modAdd(MOD - 1, 1));
        assertEquals(4, modMath.modAdd(-3, 7)); // handles negative input
    }

    @Test
    void testModSub() {
        assertEquals(2, modMath.modSub(5, 3));
        assertEquals(MOD - 1, modMath.modSub(0, 1));
        assertEquals(3, modMath.modSub(1, -2));
    }

    @Test
    void testModMul() {
        assertEquals(6, modMath.modMul(2, 3));
        assertEquals(0, modMath.modMul(123456, 0));
        assertEquals(991358087, modMath.modMul(999999937, 123456));
    }

    @Test
    void testModPow() {
        assertEquals(1024, modMath.modPow(2, 10));
        assertEquals(1, modMath.modPow(123, 0));
        assertEquals(123, modMath.modPow(123, 1));
        assertThrows(IllegalArgumentException.class, () -> modMath.modPow(2, -1));
    }

    @Test
    void testModInverse() {
        assertEquals(500000004, modMath.modInverse(2)); // 2 * 500000004 % MOD == 1
        assertEquals(1, modMath.modInverse(1));
        assertThrows(ArithmeticException.class, () -> modMath.modInverse(0));
    }

    @Test
    void testModDiv() {
        assertEquals(2, modMath.modDiv(4, 2)); // 4 / 2 % MOD
        assertEquals(500000004, modMath.modDiv(1, 2)); // 1 / 2 % MOD
        assertThrows(ArithmeticException.class, () -> modMath.modDiv(5, 0));
    }

    @Test
    void testGetMod() {
        assertEquals(MOD, modMath.getMod());
    }

    @Test
    void testConstructorInvalidMod() {
        assertThrows(IllegalArgumentException.class, () -> new ModMath(0));
        assertThrows(IllegalArgumentException.class, () -> new ModMath(-1));
    }
}
