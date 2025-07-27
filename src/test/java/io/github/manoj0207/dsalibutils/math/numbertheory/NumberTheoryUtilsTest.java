package io.github.manoj0207.dsalibutils.math.numbertheory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberTheoryUtilsTest {

    @Test
    void testGcd() {
        assertEquals(5, NumberTheoryUtils.gcd(10, 5));
        assertEquals(1, NumberTheoryUtils.gcd(17, 31));
        assertEquals(12, NumberTheoryUtils.gcd(0, 12));
        assertEquals(7, NumberTheoryUtils.gcd(-14, 21));
    }

    @Test
    void testLcm() {
        assertEquals(20, NumberTheoryUtils.lcm(4, 5));
        assertEquals(0, NumberTheoryUtils.lcm(0, 7));
        assertEquals(0, NumberTheoryUtils.lcm(3, 0));
        assertEquals(84, NumberTheoryUtils.lcm(12, 21));
        assertThrows(ArithmeticException.class, () -> NumberTheoryUtils.lcm(Integer.MAX_VALUE, 2));
    }

    @Test
    void testExtendedGCD() {
        NumberTheoryUtils.GCDTriplet result = NumberTheoryUtils.extendedGCD(30, 20);
        assertEquals(10, result.gcd);
        assertEquals(result.gcd, 30 * result.x + 20 * result.y);
        // Validate identity: ax + by = gcd

        result = NumberTheoryUtils.extendedGCD(101, 103);
        assertEquals(1, result.gcd);
        assertEquals(result.gcd, 101 * result.x + 103 * result.y);
    }

    @Test
    void testModInverse() {
        assertEquals(3, NumberTheoryUtils.modInverse(2, 5)); // (2 * 3) % 5 == 1
        assertEquals(500000004, NumberTheoryUtils.modInverse(2, 1_000_000_007));

        // Negative result mod handling
        int inv = NumberTheoryUtils.modInverse(7, 13);
        assertEquals(2, inv); // (7 * 2) % 13 == 1

        // Mod inverse doesn't exist
        assertThrows(ArithmeticException.class, () -> NumberTheoryUtils.modInverse(6, 12));
        assertThrows(ArithmeticException.class, () -> NumberTheoryUtils.modInverse(5, 0));
    }
}
