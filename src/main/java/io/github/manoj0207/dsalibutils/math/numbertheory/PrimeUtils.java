package io.github.manoj0207.dsalibutils.math.numbertheory;

import java.util.*;

/**
 * Utility class providing methods for various prime-related computations,
 * including Sieve of Eratosthenes, prime factorization, segmented sieve, and
 * primality testing.
 */
public class PrimeUtils {

    /**
     * Generates a boolean array where {@code true} denotes that the index is prime using
     * the Sieve of Eratosthenes algorithm.
     *
     * @param n the upper bound (inclusive)
     * @return boolean array of size {@code n + 1} indicating primality of each number
     * @throws IllegalArgumentException if {@code n < 0}
     */
    public static boolean[] simpleSieve(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input must be non-negative.");
        }

        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        if (n >= 0) isPrime[0] = false;
        if (n >= 1) isPrime[1] = false;

        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        return isPrime;
    }

    /**
     * Precomputes the smallest prime factor (SPF) for each number up to {@code n}.
     *
     * @param n the upper bound (inclusive)
     * @return an array {@code spf} such that {@code spf[i]} is the smallest prime factor of {@code i}
     * @throws IllegalArgumentException if {@code n < 1}
     */
    public static int[] sieveForPrimeFactorization(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Input must be at least 1.");
        }

        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i;

        for (int i = 2; i * i <= n; i++) {
            if (spf[i] == i) {
                for (int j = i * i; j <= n; j += i) {
                    if (spf[j] == j) {
                        spf[j] = i;
                    }
                }
            }
        }

        return spf;
    }

    /**
     * Computes the list of prime factors of a number using a precomputed SPF array.
     *
     * @param x   the number to factorize (must be ≥ 2)
     * @param spf the SPF array computed by {@link #sieveForPrimeFactorization}
     * @return a list of prime factors of {@code x}
     * @throws IllegalArgumentException if {@code x < 2} or {@code spf == null || spf.length <= x}
     */
    public static List<Integer> getPrimeFactors(int x, int[] spf) {
        if (x < 2) {
            throw new IllegalArgumentException("x must be ≥ 2.");
        }
        if (spf == null || spf.length <= x) {
            throw new IllegalArgumentException("SPF array must be precomputed and of size > x.");
        }

        List<Integer> factors = new ArrayList<>();
        while (x != 1) {
            factors.add(spf[x]);
            x /= spf[x];
        }
        return factors;
    }

    /**
     * Computes prime numbers in the range [m, n] using the Segmented Sieve algorithm.
     *
     * @param m the lower bound (inclusive)
     * @param n the upper bound (inclusive)
     * @return a boolean array of size {@code (n - m + 1)} where {@code true} means the number is prime
     * @throws IllegalArgumentException if {@code m < 1 || m > n}
     */
    public static boolean[] segmentedSieve(int m, int n) {
        if (m < 1 || m > n) {
            throw new IllegalArgumentException("Invalid range: m must be ≥ 1 and ≤ n.");
        }

        int limit = (int) Math.sqrt(n);
        boolean[] isPrimeSmall = simpleSieve(limit);

        List<Integer> basePrimes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrimeSmall[i]) basePrimes.add(i);
        }

        boolean[] isPrimeSegment = new boolean[n - m + 1];
        Arrays.fill(isPrimeSegment, true);

        for (int prime : basePrimes) {
            int start = Math.max(prime * prime, ((m + prime - 1) / prime) * prime);
            for (int j = start; j <= n; j += prime) {
                isPrimeSegment[j - m] = false;
            }
        }

        if (m == 1) isPrimeSegment[0] = false;
        return isPrimeSegment;
    }

    /**
     * Checks if a given integer is a prime number using trial division.
     *
     * @param x the number to check
     * @return {@code true} if {@code x} is a prime number; {@code false} otherwise
     */
    public static boolean isPrime(int x) {
        if (x < 2)
            return false;
        int sq = (int)Math.sqrt(x);
        for (int i = 2; i <= sq; i++) {
            if (x % i == 0) return false;
        }
        return true;
    }
}
