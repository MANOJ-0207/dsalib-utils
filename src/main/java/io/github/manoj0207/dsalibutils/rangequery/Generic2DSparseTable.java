package io.github.manoj0207.dsalibutils.rangequery;

import java.util.List;
import java.util.function.BinaryOperator;

/**
 * A generic 2D Sparse Table implementation for fast range queries over static 2D data.
 *
 * <p>Supports idempotent and overlap-safe operations like {@code min}, {@code max}, {@code gcd}.
 * This structure is read-only and does not support updates after construction.</p>
 *
 * <p>Internally uses a 4D precomputed table for O(1) query time.</p>
 *
 * @param <T> the type of elements stored in the 2D matrix
 */
public class Generic2DSparseTable<T> {

    private final T[][][][] table;
    private final BinaryOperator<T> operation;
    private final int rows, cols;
    private final int maxRowPow, maxColPow;

    /**
     * Constructs the 2D Sparse Table from a rectangular 2D array using a binary idempotent operation.
     *
     * @param input     the input 2D array (non-null, non-empty, rectangular)
     * @param operation an idempotent and overlap-safe binary operation (e.g., {@code Math::min})
     *
     * @throws IllegalArgumentException if input is null, empty, jagged, or operation is null
     *
     * <p><b>Time Complexity:</b> <br>
     * <p>Preprocessing takes O(n × m × log n × log m)</p>
     */
    @SuppressWarnings("unchecked")
    public Generic2DSparseTable(T[][] input, BinaryOperator<T> operation) {
        if (input == null || input.length == 0 || input[0] == null || input[0].length == 0)
            throw new IllegalArgumentException("Input matrix must not be null or empty");

        int m = input.length, n = input[0].length;

        for (T[] row : input)
            if (row == null || row.length != n)
                throw new IllegalArgumentException("All rows must be non-null and of equal length");

        if (operation == null)
            throw new IllegalArgumentException("Operation must not be null");

        this.rows = m;
        this.cols = n;
        this.operation = operation;

        this.maxRowPow = 32 - Integer.numberOfLeadingZeros(m);
        this.maxColPow = 32 - Integer.numberOfLeadingZeros(n);

        this.table = (T[][][][]) new Object[m][n][maxRowPow][maxColPow];

        // Base case initialization
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                table[i][j][0][0] = input[i][j];

        // Preprocess columns
        for (int i = 0; i < m; i++) {
            for (int y = 1; (1 << y) <= n; y++) {
                for (int j = 0; j + (1 << y) <= n; j++) {
                    table[i][j][0][y] = operation.apply(
                            table[i][j][0][y - 1],
                            table[i][j + (1 << (y - 1))][0][y - 1]
                    );
                }
            }
        }

        // Preprocess rows
        for (int x = 1; (1 << x) <= m; x++) {
            for (int i = 0; i + (1 << x) <= m; i++) {
                for (int y = 0; (1 << y) <= n; y++) {
                    for (int j = 0; j + (1 << y) <= n; j++) {
                        table[i][j][x][y] = operation.apply(
                                table[i][j][x - 1][y],
                                table[i + (1 << (x - 1))][j][x - 1][y]
                        );
                    }
                }
            }
        }
    }

    /**
     * Alternate constructor that accepts a {@code List<List<T>>} instead of a 2D array.
     *
     * @param input     the input 2D list (non-null, rectangular)
     * @param operation the idempotent and overlap-safe binary operation
     *
     * @throws IllegalArgumentException if input is null, empty, jagged, or operation is null
     *
     * <p><b>Time Complexity:</b> <p>Same as array-based constructor: O(n × m × log n × log m)</p>
     */
    public Generic2DSparseTable(List<List<T>> input, BinaryOperator<T> operation) {
        this(listTo2DArray(input), operation);
    }

    /**
     * Queries the result of applying the operation over the submatrix from (x1, y1) to (x2, y2), inclusive.
     *
     * @param x1 top-left row index
     * @param y1 top-left column index
     * @param x2 bottom-right row index
     * @param y2 bottom-right column index
     * @return the combined result of applying the operation on the submatrix
     *
     * @throws IndexOutOfBoundsException if indices are invalid or out of bounds
     *
     * <p><b>Time Complexity:</b> <p>O(1)</p> due to precomputed 4-cell aggregation</p>
     */
    public T query(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || y1 < 0 || x2 >= rows || y2 >= cols || x1 > x2 || y1 > y2)
            throw new IndexOutOfBoundsException("Invalid query bounds");

        int kx = 31 - Integer.numberOfLeadingZeros(x2 - x1 + 1);
        int ky = 31 - Integer.numberOfLeadingZeros(y2 - y1 + 1);

        T a = table[x1][y1][kx][ky];
        T b = table[x2 - (1 << kx) + 1][y1][kx][ky];
        T c = table[x1][y2 - (1 << ky) + 1][kx][ky];
        T d = table[x2 - (1 << kx) + 1][y2 - (1 << ky) + 1][kx][ky];

        return operation.apply(operation.apply(a, b), operation.apply(c, d));
    }

    /**
     * Converts a List of Lists to a 2D array.
     *
     * @param list the 2D list to convert
     * @param <T>  the type of elements
     * @return the equivalent 2D array
     *
     * @throws IllegalArgumentException if list is null, empty, or jagged
     */
    @SuppressWarnings("unchecked")
    private static <T> T[][] listTo2DArray(List<List<T>> list) {
        if (list == null || list.isEmpty() || list.get(0) == null || list.get(0).isEmpty())
            throw new IllegalArgumentException("Input list must not be null or empty");

        int m = list.size();
        int n = list.get(0).size();

        for (List<T> row : list)
            if (row == null || row.size() != n)
                throw new IllegalArgumentException("All rows must be non-null and of equal length");

        T[][] result = (T[][]) new Object[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                result[i][j] = list.get(i).get(j);

        return result;
    }
}
