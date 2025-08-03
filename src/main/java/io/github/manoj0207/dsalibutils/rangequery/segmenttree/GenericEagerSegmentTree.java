package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.Arrays;

/**
 * Generic segment tree supporting eager propagation (no lazy updates).
 * <p>
 * Handles point updates and range queries over immutable or safely mutable data.
 * </p>
 *
 * <p><b>Supports:</b></p>
 * <ul>
 *     <li>Point update via direct value replacement</li>
 *     <li>Point update via transformation function</li>
 *     <li>Range update via transformation function (eager propagation)</li>
 * </ul>
 *
 * @param <T> the type of elements managed by the segment tree
 */
public class GenericEagerSegmentTree<T> implements SegmentTree<T> {

    private final T[] tree;
    private final T[] input;
    private final int n;
    private final BinaryOperator<T> operation;
    private final T defaultValue;

    /**
     * Constructs a segment tree from an array.
     *
     * @param input         the input array
     * @param operation     the binary operator to combine values (e.g. sum, min, max)
     * @param defaultValue  the identity element for the operation
     * @throws IllegalArgumentException if input is null/empty or operation is null
     */
    @SuppressWarnings("unchecked")
    public GenericEagerSegmentTree(T[] input, BinaryOperator<T> operation, T defaultValue) {
        if (input == null || input.length == 0)
            throw new IllegalArgumentException("Input array must not be null or empty.");
        if (operation == null)
            throw new IllegalArgumentException("Binary operation must not be null.");

        this.n = input.length;
        this.input = Arrays.copyOf(input, n);
        this.operation = operation;
        this.defaultValue = defaultValue;

        int height = (int) Math.ceil(Math.log(n) / Math.log(2));
        int size = 2 * (1 << height) - 1;
        this.tree = (T[]) new Object[size];

        build(0, n - 1, 0);
    }

    /**
     * Constructs a segment tree from a list.
     *
     * @param inputList     the list of input values
     * @param operation     the binary operator to combine values
     * @param defaultValue  the identity element for the operation
     * @throws IllegalArgumentException if input list is null or empty
     */
    public GenericEagerSegmentTree(List<T> inputList, BinaryOperator<T> operation, T defaultValue) {
        this(
                inputList == null ? null : inputList.toArray((T[]) new Object[0]),
                operation,
                defaultValue
        );
    }

    /**
     * Recursively builds the segment tree from the input array.
     *
     * @param start the start index in input
     * @param end   the end index in input
     * @param node  the current tree node index
     */
    private void build(int start, int end, int node) {
        if (start == end) {
            tree[node] = input[start];
            return;
        }

        int mid = (start + end) >>> 1;
        build(start, mid, 2 * node + 1);
        build(mid + 1, end, 2 * node + 2);
        tree[node] = operation.apply(tree[2 * node + 1], tree[2 * node + 2]);
    }

    /**
     * Queries the result of the operation over range [left, right].
     *
     * @param left  the starting index (inclusive)
     * @param right the ending index (inclusive)
     * @return the result of combining values in the range
     * @throws IndexOutOfBoundsException if indices are out of range
     *
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    @Override
    public T query(int left, int right) {
        if (left < 0 || right >= n || left > right)
            throw new IndexOutOfBoundsException("Invalid range: [" + left + ", " + right + "]");
        return queryUtil(0, n - 1, left, right, 0);
    }

    private T queryUtil(int start, int end, int left, int right, int node) {
        if (start > right || end < left) return defaultValue;
        if (start >= left && end <= right) return tree[node];

        int mid = (start + end) >>> 1;
        T leftResult = queryUtil(start, mid, left, right, 2 * node + 1);
        T rightResult = queryUtil(mid + 1, end, left, right, 2 * node + 2);
        return operation.apply(leftResult, rightResult);
    }

    /**
     * Updates the value at a specific index.
     *
     * @param index the index to update
     * @param value the new value to set
     * @throws IndexOutOfBoundsException if the index is out of range
     *
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    @Override
    public void update(int index, T value) {
        if (index < 0 || index >= n)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        updateUtil(0, n - 1, index, value, 0);
    }

    /**
     * Updates the value at a given index using a transformation function.
     *
     * @param index   the index to update
     * @param updater a function that takes current value and returns updated value
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws IllegalArgumentException if updater is null
     *
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    @Override
    public void update(int index, Function<T, T> updater) {
        if (updater == null)
            throw new IllegalArgumentException("Update function must not be null.");
        T oldValue = query(index, index);
        T newValue = updater.apply(oldValue);
        update(index, newValue);
    }

    private void updateUtil(int start, int end, int index, T value, int node) {
        if (start == end) {
            tree[node] = value;
            return;
        }

        int mid = (start + end) >>> 1;
        if (index <= mid) {
            updateUtil(start, mid, index, value, 2 * node + 1);
        } else {
            updateUtil(mid + 1, end, index, value, 2 * node + 2);
        }

        tree[node] = operation.apply(tree[2 * node + 1], tree[2 * node + 2]);
    }

    /**
     * Applies a transformation function to all elements in the range [l, r].
     *
     * <p>Internally performs point-wise updates in range, rebuilding affected segments eagerly.</p>
     *
     * @param l    the left index of the range (inclusive)
     * @param r    the right index of the range (inclusive)
     * @param func the transformation function to apply
     * @throws IllegalArgumentException if {@code func} is null
     * @throws IndexOutOfBoundsException if indices are out of range
     *
     * <p><b>Time Complexity:</b> <p>O((r - l + 1) × log n)</p>
     */
    @Override
    public void update(int l, int r, Function<T, T> func) {
        if (l < 0 || r >= n || l > r)
            throw new IndexOutOfBoundsException("Invalid update range: [" + l + ", " + r + "]");
        if (func == null)
            throw new IllegalArgumentException("Update function must not be null.");

        for (int i = l; i <= r; i++) {
            T current = query(i, i);
            T updated = func.apply(current);
            update(i, updated);
        }
    }

    /**
     * Applies the given value to all elements in the range [l, r] via direct replacement.
     *
     * <p>Each index in the range is updated individually, and the affected tree segments
     * are rebuilt eagerly to maintain correctness.</p>
     *
     * @param l     the starting index of the range (inclusive)
     * @param r     the ending index of the range (inclusive)
     * @param value the value to assign at each index in the range
     *
     * @throws IndexOutOfBoundsException if {@code l < 0}, {@code r >= size()}, or {@code l > r}
     *
     * <p><b>Time Complexity:</b> O((r - l + 1) × log n)</p>
     */
    public void update(int l, int r, T value) {
        if (l < 0 || r >= n || l > r)
            throw new IndexOutOfBoundsException("Invalid update range: [" + l + ", " + r + "]");

        for (int i = l; i <= r; i++) {
            update(i, value);
        }
    }


    /**
     * Returns the number of elements in the segment tree.
     *
     * @return the size of the original input array
     */
    @Override
    public int size() {
        return n;
    }

    /**
     * Prints the internal segment tree representation (for debugging).
     */
    public void printTree() {
        System.out.println("Segment Tree: " + Arrays.toString(tree));
    }
}
