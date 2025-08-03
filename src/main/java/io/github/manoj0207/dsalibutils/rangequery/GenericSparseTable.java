package io.github.manoj0207.dsalibutils.rangequery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * A generic Sparse Table implementation for static range queries.
 *
 * <p>Supports fast queries on immutable data using idempotent or overlap-safe operations like
 * <b>min</b>, <b>max</b>, <b>gcd</b>. For non-overlap-safe operations like sum, it performs
 * a disjoint interval merge with O(log n) time.</p>
 *
 * @param <T> the type of elements (must be safely sharable or immutable)
 */
public class GenericSparseTable<T> {

    private final int n;
    private final int log;
    private final T[][] table;
    private final BinaryOperator<T> operation;
    private final boolean isOverlapSafe;

    /**
     * Constructs a sparse table with default assumption that the operation is not overlap-safe.
     *
     * @param input     the input array (non-null, non-empty, no null elements)
     * @param operation the binary operator used in queries (e.g., min, max, gcd, sum)
     *
     * @throws IllegalArgumentException if input is null, empty, or contains null values
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p> for preprocessing</p>
     */
    public GenericSparseTable(T[] input, BinaryOperator<T> operation) {
        this(input, operation, false);
    }

    /**
     * Constructs a sparse table from a list input with custom overlap-safe configuration.
     *
     * @param input          the input list (non-null, non-empty, no null elements)
     * @param op             the binary operator used in queries
     * @param isOverlapSafe  whether the operator supports overlapping intervals
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p> for preprocessing</p>
     */
    public GenericSparseTable(List<T> input, BinaryOperator<T> op, boolean isOverlapSafe) {
        this(input.toArray((T[]) new Object[0]), op, isOverlapSafe);
    }

    /**
     * Constructs a sparse table from an array with custom overlap-safe configuration.
     *
     * @param input          the input array (non-null, non-empty, no null elements)
     * @param operation      the binary operator used in queries
     * @param isOverlapSafe  whether the operation supports overlapping intervals
     *
     * @throws IllegalArgumentException if input is null, empty, or contains null values
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p> for preprocessing</p>
     */
    @SuppressWarnings("unchecked")
    public GenericSparseTable(T[] input, BinaryOperator<T> operation, boolean isOverlapSafe) {
        if (input == null) throw new IllegalArgumentException("Input array must be non-null");
        if (input.length == 0) throw new IllegalArgumentException("Input array must be non-empty");
        if (operation == null) throw new IllegalArgumentException("Operation cannot be null");
        for (T item : input) {
            if (item == null)
                throw new IllegalArgumentException("Input array cannot contain null elements");
        }

        this.n = input.length;
        this.operation = operation;
        this.isOverlapSafe = isOverlapSafe;
        this.log = 32 - Integer.numberOfLeadingZeros(n);
        this.table = (T[][]) new Object[log][n];

        // Initialize base level
        System.arraycopy(input, 0, table[0], 0, n);

        // Preprocess all log levels
        for (int k = 1; k < log; k++) {
            for (int i = 0; i + (1 << k) <= n; i++) {
                table[k][i] = operation.apply(table[k - 1][i], table[k - 1][i + (1 << (k - 1))]);
            }
        }
    }

    /**
     * Queries the result over the range [l, r] using the given operation.
     *
     * @param l the starting index (inclusive)
     * @param r the ending index (inclusive)
     * @return the result of applying the operation on the subarray [l, r]
     *
     * @throws IllegalArgumentException if l or r are out of bounds or l > r
     *
     * <p><b>Time Complexity:</b>
     * <ul>
     *   <li><b>O(1)</b> if overlap-safe (e.g., min/max/gcd)</li>
     *   <li><b>O(log n)</b> otherwise</li>
     * </ul>
     * </p>
     */
    public T query(int l, int r) {
        if (l < 0 || r >= n || l > r)
            throw new IllegalArgumentException("Invalid query range: [" + l + ", " + r + "]");

        if (isOverlapSafe) {
            int k = 31 - Integer.numberOfLeadingZeros(r - l + 1);
            return operation.apply(table[k][l], table[k][r - (1 << k) + 1]);
        } else {
            T result = null;
            int length = r - l + 1;
            for (int k = log - 1; k >= 0; k--) {
                if ((1 << k) <= length) {
                    result = (result == null) ? table[k][l] : operation.apply(result, table[k][l]);
                    l += 1 << k;
                    length -= 1 << k;
                }
            }
            return result;
        }
    }

    /**
     * Returns the precomputed internal table.
     *
     * <p><b>Warning:</b> This returns the raw 2D array and may expose internal state.</p>
     *
     * @return the sparse table as a 2D array
     */
    public T[][] getTable() {
        return table;
    }

    /**
     * Returns the precomputed table as a list of rows.
     * Each row represents a log-level of the sparse table.
     *
     * @return list of rows representing the sparse table
     */
    public List<List<T>> getTableAsLists() {
        List<List<T>> result = new ArrayList<>();
        for (T[] row : table) {
            List<T> list = new ArrayList<>();
            if (row != null) {
                for (T val : row) list.add(val);
            }
            result.add(list);
        }
        return result;
    }

    /**
     * Returns whether the operation used is overlap-safe.
     *
     * @return {@code true} if overlap-safe; otherwise {@code false}
     */
    public boolean isOverlapSafe() {
        return isOverlapSafe;
    }

    /**
     * Returns the size of the original input array.
     *
     * @return the number of elements
     */
    public int size() {
        return n;
    }
}
