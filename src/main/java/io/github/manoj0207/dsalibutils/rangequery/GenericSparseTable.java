package io.github.manoj0207.dsalibutils.rangequery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Generic Sparse Table implementation.
 *
 * Supports fast range queries using overlap-safe operations like min, max, gcd.
 * Falls back to O(log n) querying if operation is not overlap-safe (like sum).
 *
 * @param <T> Type of the elements (should be immutable or safely shared)
 */
public class GenericSparseTable<T> {

    private final int n;
    private final int log;
    private final T[][] table;
    private final BinaryOperator<T> operation;
    private final boolean isOverlapSafe;

    /**
     * Constructs a sparse table assuming operation is not overlap-safe.
     *
     * @param input     input array (must be non-null and non-empty)
     * @param operation binary function for the query (e.g. min, gcd, sum)
     */
    public GenericSparseTable(T[] input, BinaryOperator<T> operation) {
        this(input, operation, false); // Default: overlap not safe
    }

    public GenericSparseTable(List<T> input, BinaryOperator<T> op, boolean isOverlapSafe) {
        this(input.toArray((T[]) new Object[0]), op, isOverlapSafe);
    }
    /**
     * Constructs a sparse table with control over overlap safety.
     *
     * @param input           input array (must be non-null and non-empty)
     * @param operation       binary function for the query (e.g. min, gcd, sum)
     * @param isOverlapSafe   true if operation supports overlap-safe querying
     */
    @SuppressWarnings("unchecked")
    public GenericSparseTable(T[] input, BinaryOperator<T> operation, boolean isOverlapSafe) {
        if (input == null)
            throw new IllegalArgumentException("Input array must be non-null");
        if (input.length == 0)
            throw new IllegalArgumentException("Input array must be non-empty");
        if (operation == null)
            throw new IllegalArgumentException("Operation cannot be null");
        for (T item : input)
            if (item == null)
                throw new IllegalArgumentException("Input array cannot contain null elements");

        this.n = input.length;
        this.operation = operation;
        this.isOverlapSafe = isOverlapSafe;
        this.log = 32 - Integer.numberOfLeadingZeros(n);
        this.table = (T[][]) new Object[log][n];

        // Base layer
        System.arraycopy(input, 0, table[0], 0, n);

        // Build sparse table
        for (int k = 1; k < log; k++) {
            for (int i = 0; i + (1 << k) <= n; i++) {
                table[k][i] = operation.apply(table[k - 1][i], table[k - 1][i + (1 << (k - 1))]);
            }
        }
    }


    /**
     * Answers the query in range [l, r] (both inclusive).
     *
     * - If the operation is overlap-safe (like min/max/gcd), O(1) query.
     * - Otherwise, fallback to disjoint interval processing in O(log n).
     *
     * @param l Left index (inclusive)
     * @param r Right index (inclusive)
     * @return result of applying the operation from index l to r
     */
    public T query(int l, int r) {
        if (l < 0 || r >= n || l > r)
            throw new IllegalArgumentException("Invalid query range: [" + l + ", " + r + "]");

        if (isOverlapSafe) {
            int k = 31 - Integer.numberOfLeadingZeros(r - l + 1);
            return operation.apply(table[k][l], table[k][r - (1 << k) + 1]);
        } else {
            // Proper disjoint interval decomposition (like segment tree logic)
            T result = null;
            int length = r - l + 1;
            for (int k = log - 1; k >= 0; k--) {
                if ((1 << k) <= length) {
                    if (result == null) {
                        result = table[k][l];
                    } else {
                        result = operation.apply(result, table[k][l]);
                    }
                    l += 1 << k;
                    length -= 1 << k;
                }
            }
            return result;
        }
    }


    /**
     * Returns precomputed table (read-only). Useful for debugging.
     */
    public T[][] getTable() {
        return table;
    }

    /**
     * Returns the precomputed table as a list of rows (each row is a list).
     * Safer alternative to avoid exposing raw generic arrays.
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
     * Checks whether the operation is overlap-safe.
     */
    public boolean isOverlapSafe() {
        return isOverlapSafe;
    }

    /**
     * Returns the size of the original array.
     */
    public int size() {
        return n;
    }
}

