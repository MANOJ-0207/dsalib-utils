package io.github.manoj0207.dsalibutils.math.numbertheory;

/**
 * Utility class providing various number theory operations such as GCD, LCM,
 * Extended Euclidean Algorithm, and modular inverse computation.
 */
public class NumberTheoryUtils {

    /**
     * <p><b>Time Complexity:</b> O(log min(a, b))</p>
     *
     * Computes the Greatest Common Divisor (GCD) of two integers using the Euclidean algorithm.
     *
     * @param a the first number
     * @param b the second number
     * @return the non-negative greatest common divisor of {@code a} and {@code b}
     */
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    /**
     * <p><b>Time Complexity:</b> O(log min(a, b))</p>
     *
     * Computes the Least Common Multiple (LCM) of two integers.
     *
     * @param a the first number
     * @param b the second number
     * @return the least common multiple of {@code a} and {@code b}
     * @throws ArithmeticException if the result overflows {@code int}
     */
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        long lcm = Math.abs((1L * a / gcd(a, b)) * b);
        if (lcm > Integer.MAX_VALUE) {
            throw new ArithmeticException("LCM overflows 32-bit signed integer range.");
        }
        return (int) lcm;
    }

    /**
     * A helper class representing the result of the Extended Euclidean Algorithm.
     * It contains the GCD of two integers, and the coefficients {@code x} and {@code y}
     * such that {@code a * x + b * y = gcd(a, b)}.
     */
    public static class GCDTriplet {
        /** The greatest common divisor of the input integers. */
        public final int gcd;

        /** Coefficient of the first integer (a). */
        public final int x;

        /** Coefficient of the second integer (b). */
        public final int y;

        /**
         * Constructs the result triplet of the Extended Euclidean Algorithm.
         *
         * @param gcd the greatest common divisor
         * @param x   the coefficient for the first integer
         * @param y   the coefficient for the second integer
         */
        public GCDTriplet(int gcd, int x, int y) {
            this.gcd = gcd;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * <p><b>Time Complexity:</b> O(log min(a, b))</p>
     *
     * Computes the Extended Euclidean Algorithm for two integers.
     *
     * <p>Finds {@code x}, {@code y}, and {@code gcd} such that: {@code a * x + b * y = gcd(a, b)}.</p>
     *
     * @param a the first integer
     * @param b the second integer
     * @return a {@link GCDTriplet} containing gcd(a, b) and coefficients x and y
     */
    public static GCDTriplet extendedGCD(int a, int b) {
        if (b == 0) return new GCDTriplet(a, 1, 0);
        GCDTriplet result = extendedGCD(b, a % b);
        int x1 = result.y;
        int y1 = result.x - (a / b) * result.y;
        return new GCDTriplet(result.gcd, x1, y1);
    }

    /**
     * <p><b>Time Complexity:</b> O(log mod)</p>
     *
     * Computes the modular inverse of {@code a} modulo {@code mod} using the Extended Euclidean Algorithm.
     * The modular inverse exists only if {@code gcd(a, mod) == 1}.
     *
     * @param a   the number to find the inverse of
     * @param mod the modulus (must be > 0)
     * @return the modular inverse of {@code a} modulo {@code mod}
     * @throws ArithmeticException if {@code mod ≤ 0} or modular inverse does not exist
     */
    public static int modInverse(int a, int mod) {
        if (mod <= 0) {
            throw new ArithmeticException("Modulus must be a positive integer.");
        }
        GCDTriplet result = extendedGCD(a, mod);
        if (result.gcd != 1) {
            throw new ArithmeticException("Modular inverse doesn't exist for a = " + a + ", mod = " + mod);
        }
        return (result.x % mod + mod) % mod;
    }
}
