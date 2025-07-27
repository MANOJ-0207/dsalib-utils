package io.github.manoj0207.dsalibutils.math.numbertheory;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimeUtilsTest {

    @Test
    void testSimpleSieve() {
        boolean[] sieve = PrimeUtils.simpleSieve(10);
        assertFalse(sieve[0]);
        assertFalse(sieve[1]);
        assertTrue(sieve[2]);
        assertTrue(sieve[3]);
        assertFalse(sieve[4]);
        assertTrue(sieve[5]);
        assertFalse(sieve[6]);
        assertTrue(sieve[7]);
        assertFalse(sieve[8]);
        assertFalse(sieve[9]);
        assertFalse(sieve[10]);

        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.simpleSieve(-1));
    }

    @Test
    void testSieveForPrimeFactorization() {
        int[] spf = PrimeUtils.sieveForPrimeFactorization(15);
        // Expected: spf[i] == smallest prime factor of i
        assertEquals(2, spf[4]); // 2*2
        assertEquals(3, spf[9]); // 3*3
        assertEquals(2, spf[10]); // 2*5 -> smallest is 2
        assertEquals(13, spf[13]); // 13 -> prime
        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.sieveForPrimeFactorization(0));
    }

    @Test
    void testGetPrimeFactors() {
        int[] spf = PrimeUtils.sieveForPrimeFactorization(100);
        List<Integer> factors = PrimeUtils.getPrimeFactors(60, spf);
        assertEquals(List.of(2, 2, 3, 5), factors); // 2*2*3*5

        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.getPrimeFactors(1, spf));
        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.getPrimeFactors(101, spf));
        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.getPrimeFactors(10, null));
    }

    @Test
    void testSegmentedSieve() {
        boolean[] primes = PrimeUtils.segmentedSieve(10, 20);
        List<Integer> expectedPrimes = List.of(11, 13, 17, 19);
        for (int i = 0; i < primes.length; i++) {
            int num = i + 10;
            if (expectedPrimes.contains(num)) {
                assertTrue(primes[i], num + " should be prime");
            } else {
                assertFalse(primes[i], num + " should not be prime");
            }
        }

        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.segmentedSieve(0, 10));
        assertThrows(IllegalArgumentException.class, () -> PrimeUtils.segmentedSieve(20, 10));
    }

    @Test
    void testIsPrime() {
        assertFalse(PrimeUtils.isPrime(0));
        assertFalse(PrimeUtils.isPrime(1));
        assertTrue(PrimeUtils.isPrime(2));
        assertTrue(PrimeUtils.isPrime(17));
        assertFalse(PrimeUtils.isPrime(100));
        assertTrue(PrimeUtils.isPrime(9973));
    }
}
