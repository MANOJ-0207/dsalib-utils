package io.github.manoj0207.dsalibutils.math.combinatorics;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to work with Pascal's Triangle.
 *
 * <p>Provides methods to:</p>
 * <ul>
 *   <li>Build the entire triangle</li>
 *   <li>Fetch a specific row</li>
 *   <li>Compute individual binomial coefficients</li>
 *   <li>Find the peak (maximum) value in a given row</li>
 * </ul>
 *
 * <p>Limits are enforced to prevent integer overflow. Internally all computations are done with {@code long}
 * but returned as {@code int} after bounds checking.</p>
 */
public class PascalTriangleUtils {

    /** Max row index (inclusive) to prevent integer overflow */
    private static final int MAX_SAFE_N = 33;

    /**
     * <p><b>Time Complexity:</b> O(n²)</p>
     *
     * Builds Pascal's Triangle up to {@code n} rows (0-indexed).
     *
     * @param n the number of rows to build (must be non-negative)
     * @return list of rows, where each row is a list of integers representing binomial coefficients
     * @throws IllegalArgumentException if {@code n} is negative
     * @throws ArithmeticException if integer overflow occurs during triangle construction
     */
    public static List<List<Integer>> buildTriangle(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Row count must be non-negative.");
        }

        List<List<Integer>> triangle = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            List<Integer> row = new ArrayList<>();
            row.add(1); // First element is always 1

            for (int j = 1; j < i; j++) {
                int a = triangle.get(i - 1).get(j - 1);
                int b = triangle.get(i - 1).get(j);

                long sum = (long) a + b;
                if (sum > Integer.MAX_VALUE) {
                    throw new ArithmeticException("Overflow while building Pascal's Triangle.");
                }

                row.add((int) sum);
            }

            if (i > 0) row.add(1); // Last element is 1 if row has more than 1 element
            triangle.add(row);
        }

        return triangle;
    }

    /**
     * <p><b>Time Complexity:</b> O(n)</p>
     *
     * Returns the {@code n}th row of Pascal's Triangle (0-indexed).
     * Uses iterative computation of binomial coefficients to avoid full triangle build.
     *
     * @param n index of the row to generate
     * @return a list representing the {@code n}th row
     * @throws IllegalArgumentException if {@code n} is negative or exceeds {@code MAX_SAFE_N}
     */
    public static List<Integer> getNthRow(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Row index must be non-negative.");
        }
        if (n > MAX_SAFE_N) {
            throw new IllegalArgumentException("Row too large; may cause integer overflow.");
        }

        List<Integer> row = new ArrayList<>();
        long val = 1;
        for (int i = 0; i <= n; i++) {
            row.add((int) val);
            val = val * (n - i) / (i + 1);
        }
        return row;
    }

    /**
     * <p><b>Time Complexity:</b> O(k)</p>
     *
     * Returns the binomial coefficient C(row, col).
     * This is equivalent to the value at position (row, col) in Pascal’s Triangle.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return the binomial coefficient C(row, col), or 0 if col is out of bounds
     * @throws IllegalArgumentException if {@code row} is negative or greater than {@code MAX_SAFE_N}
     */
    public static int getValue(int row, int col) {
        if (row < 0) {
            throw new IllegalArgumentException("Row index must be non-negative.");
        }
        if (row > MAX_SAFE_N) {
            throw new IllegalArgumentException("Row too large; may cause integer overflow.");
        }
        if (col < 0 || col > row) return 0;

        long val = 1;
        for (int i = 0; i < col; i++) {
            val = val * (row - i) / (i + 1);
        }
        return (int) val;
    }

    /**
     * <p><b>Time Complexity:</b> O(row)</p>
     *
     * Returns the peak (maximum) value in the given row of Pascal's Triangle.
     * For even rows, this is the center value; for odd rows, one of the two middle values.
     *
     * @param row index of the row (0-based)
     * @return the peak value in that row
     * @throws IllegalArgumentException if {@code row} is negative or exceeds {@code MAX_SAFE_N}
     */
    public static int getPeak(int row) {
        return getValue(row, row / 2);
    }
}
