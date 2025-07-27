package io.github.manoj0207.dsalibutils.math.combinatorics;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to work with Pascal's Triangle.
 *
 * Provides methods to build the full triangle, fetch a specific row,
 * calculate individual binomial coefficient, and determine the peak value
 * in a given row.
 */
public class PascalTriangleUtils {

    private static final int MAX_SAFE_N = 33; // For safe int overflow in getNthRow

    /**
     * Builds Pascal's Triangle up to {@code n} rows (0-indexed).
     *
     * @param n the number of rows to build (must be non-negative)
     * @return list of rows, where each row is a list of integers representing binomial coefficients
     * @throws IllegalArgumentException if {@code n} is negative or too large to safely compute
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

                // Check for overflow
                if ((long) a + b > Integer.MAX_VALUE) {
                    throw new ArithmeticException("Overflow while building Pascal's Triangle.");
                }

                row.add(a + b);
            }

            if (i > 0) row.add(1); // Last element is 1 if row has more than 1 element
            triangle.add(row);
        }

        return triangle;
    }

    /**
     * Returns the {@code n}th row of Pascal's Triangle (0-indexed).
     *
     * @param n index of the row to generate
     * @return a list representing the {@code n}th row
     * @throws IllegalArgumentException if {@code n} is negative or exceeds safe integer limit
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
     * Returns the binomial coefficient at given row and column in Pascal's Triangle.
     * Equivalent to {@code C(row, col)}.
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return the binomial coefficient at the specified position, or 0 if {@code col < 0 || col > row}
     * @throws IllegalArgumentException if {@code row} is negative or too large to compute safely
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
     * Returns the maximum value (peak) in the given row of Pascal's Triangle.
     * For even rows, this is the middle element; for odd rows, one of the two middle elements.
     *
     * @param row index of the row (0-indexed)
     * @return the peak value in the row
     * @throws IllegalArgumentException if {@code row} is negative or exceeds safe integer limit
     */
    public static int getPeak(int row) {
        return getValue(row, row / 2);
    }
}
