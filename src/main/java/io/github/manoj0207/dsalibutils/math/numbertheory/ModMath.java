package io.github.manoj0207.dsalibutils.math.numbertheory;

/**
 * Utility class for performing modular arithmetic operations safely under a given modulus.
 *
 * <p>Assumes the modulus is a positive integer. For division and modular inverse,
 * this class assumes the modulus is a prime number (as it uses Fermat's little theorem).</p>
 *
 * <p>All operations are guaranteed to return results in the range [0, mod - 1].</p>
 */
public class ModMath {
    private final int mod;

    /**
     * Constructs a modular arithmetic utility for a given modulus.
     *
     * @param mod the modulus to use (must be ≥ 1)
     * @throws IllegalArgumentException if {@code mod} is less than 1
     */
    public ModMath(int mod) {
        if (mod < 1) {
            throw new IllegalArgumentException("Modulus must be ≥ 1");
        }
        this.mod = mod;
    }

    /**
     * Performs modular addition: (a + b) % mod.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of (a + b) modulo mod
     */
    public int modAdd(int a, int b) {
        return ((a % mod + b % mod) % mod + mod) % mod;
    }

    /**
     * Performs modular subtraction: (a - b) % mod.
     *
     * @param a the minuend
     * @param b the subtrahend
     * @return the result of (a - b) modulo mod
     */
    public int modSub(int a, int b) {
        return ((a % mod - b % mod) % mod + mod) % mod;
    }

    /**
     * Performs modular multiplication: (a * b) % mod.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of (a * b) modulo mod
     */
    public int modMul(int a, int b) {
        return (int) ((((long) a % mod) * (b % mod)) % mod);
    }

    /**
     * Performs modular division: (a / b) % mod.
     *
     * <p>This is calculated as (a * b⁻¹) % mod, where b⁻¹ is the modular inverse of b.
     * Only valid if mod is a prime number and b ≠ 0.</p>
     *
     * @param a the numerator
     * @param b the denominator (must not be 0)
     * @return the result of (a / b) modulo mod
     * @throws ArithmeticException if {@code b} is 0
     */
    public int modDiv(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero in modular division.");
        }
        return modMul(a, modInverse(b));
    }

    /**
     * Computes (base^exp) % mod using fast exponentiation.
     *
     * @param base the base
     * @param exp  the exponent (must be non-negative)
     * @return the result of (base^exp) modulo mod
     * @throws IllegalArgumentException if {@code exp} is negative
     */
    public int modPow(int base, int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }

        int result = 1;
        base %= mod;

        while (exp > 0) {
            if ((exp & 1) == 1)
                result = modMul(result, base);
            base = modMul(base, base);
            exp >>= 1;
        }
        return result;
    }

    /**
     * Computes the modular inverse of {@code a}, i.e., the number x such that (a * x) % mod == 1.
     *
     * <p>Assumes {@code mod} is a prime number and {@code a ≠ 0}.
     * Uses Fermat's little theorem: a⁻¹ ≡ a^(mod - 2) mod mod</p>
     *
     * @param a the number to find the inverse of
     * @return the modular inverse of {@code a}
     * @throws ArithmeticException if {@code a} is 0
     */
    public int modInverse(int a) {
        if (a == 0) {
            throw new ArithmeticException("Cannot find modular inverse of 0.");
        }
        return modPow(a, mod - 2);
    }

    /**
     * Returns the modulus used in this utility.
     *
     * @return the modulus
     */
    public int getMod() {
        return mod;
    }
}
