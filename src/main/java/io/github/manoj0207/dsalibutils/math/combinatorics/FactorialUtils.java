package io.github.manoj0207.dsalibutils.math.combinatorics;

import io.github.manoj0207.dsalibutils.math.numbertheory.ModMath;

/**
 * Utility class for performing combinatorics operations such as nCr, nPr, Catalan numbers,
 * and derangements under a given modulus using precomputed factorials.
 *
 * <p>It supports fast modular arithmetic operations using Fermat’s Little Theorem
 * when modulus is a prime.</p>
 */
public class FactorialUtils {
    private final int[] fact;
    private final int[] invFact;
    private final int max;
    private final ModMath modMath;

    /**
     * Constructs the utility with a given modulus and precomputes factorials up to {@code max}.
     *
     * @param mod the modulus for all operations (must be a prime number)
     * @param max the maximum value (inclusive) for which to precompute factorials (must be ≥ 1)
     * @throws IllegalArgumentException if {@code max} is less than 1
     */
    public FactorialUtils(int mod, int max) {
        if (max < 1) {
            throw new IllegalArgumentException("Maximum value for factorial precomputation must be ≥ 1.");
        }
        this.modMath = new ModMath(mod);
        this.max = max + 1;
        this.fact = new int[this.max];
        this.invFact = new int[this.max];
        precompute();
    }

    /**
     * <p><b>Time Complexity:</b> O(max)</p>
     * Precomputes factorials and inverse factorials modulo the given modulus.
     */
    private void precompute() {
        fact[0] = 1;
        for (int i = 1; i < max; i++) {
            fact[i] = modMath.modMul(fact[i - 1], i);
        }

        invFact[max - 1] = modMath.modInverse(fact[max - 1]);
        for (int i = max - 2; i >= 0; i--) {
            invFact[i] = modMath.modMul(invFact[i + 1], i + 1);
        }
    }

    /**
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * Returns n! modulo mod.
     *
     * @param n the number to compute factorial for (0 ≤ n &lt;= max)
     * @return n! % mod
     * @throws IllegalArgumentException if n is out of bounds
     */
    public int factorial(int n) {
        if (n < 0 || n >= max) {
            throw new IllegalArgumentException("n must be in range [0, " + (max - 1) + "]");
        }
        return fact[n];
    }

    /**
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * Returns the modular inverse of n! modulo mod.
     *
     * @param n the number to compute inverse factorial for (0 ≤ n &lt;= max)
     * @return (n!)⁻¹ % mod
     * @throws IllegalArgumentException if n is out of bounds
     */
    public int inverseFactorial(int n) {
        if (n < 0 || n >= max) {
            throw new IllegalArgumentException("n must be in range [0, " + (max - 1) + "]");
        }
        return invFact[n];
    }

    /**
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * Computes C(n, r) = nCr % mod using precomputed factorials.
     *
     * @param n number of total items (0 ≤ n &lt;= max)
     * @param r number of chosen items (0 ≤ r ≤ n)
     * @return n choose r modulo mod, or 0 if r is invalid
     */
    public int nCr(int n, int r) {
        if (n < 0 || r < 0 || r > n || n >= max) return 0;
        return modMath.modMul(fact[n],
                modMath.modMul(invFact[r], invFact[n - r]));
    }

    /**
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * Computes P(n, r) = nPr % mod using precomputed factorials.
     *
     * @param n total number of items (0 ≤ n &lt;= max)
     * @param r number of ordered selections (0 ≤ r ≤ n)
     * @return n permute r modulo mod, or 0 if invalid
     */
    public int nPr(int n, int r) {
        if (n < 0 || r < 0 || r > n || n >= max) return 0;
        return modMath.modMul(fact[n], invFact[n - r]);
    }

    /**
     * <p><b>Time Complexity:</b> O(1)</p>
     *
     * Computes the n-th Catalan number modulo mod:
     * Catalan(n) = C(2n, n) / (n + 1)
     *
     * @param n index of the Catalan number
     * @return the nth Catalan number % mod
     * @throws IllegalArgumentException if 2n exceeds precomputed limit
     */
    public int catalan(int n) {
        if (2 * n >= max) {
            throw new IllegalArgumentException("2n must be less than precomputed limit (" + max + ")");
        }
        return modMath.modDiv(nCr(2 * n, n), n + 1);
    }

    /**
     * <p><b>Time Complexity:</b> O(n)</p>
     *
     * Computes number of derangements D(n) % mod using bottom-up dynamic programming:
     * <ul>
     *   <li>D(0) = 1</li>
     *   <li>D(1) = 0</li>
     *   <li>D(n) = (n - 1) * (D(n - 1) + D(n - 2))</li>
     * </ul>
     *
     * @param n number of elements
     * @return number of derangements of n items modulo mod
     * @throws IllegalArgumentException if n is negative
     */
    public int derangement(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Derangement is undefined for negative n.");
        }
        if (n == 0) return 1;
        if (n == 1) return 0;

        int[] D = new int[n + 1];
        D[0] = 1;
        D[1] = 0;

        for (int i = 2; i <= n; i++) {
            D[i] = modMath.modMul(i - 1,
                    modMath.modAdd(D[i - 1], D[i - 2]));
        }

        return D[n];
    }
}
