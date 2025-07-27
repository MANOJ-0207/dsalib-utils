package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.Arrays;

/**
 * Generic segment tree supporting eager propagation (no lazy updates).
 * Handles point updates and range queries over immutable or safely mutable data.
 *
 * Supports two point update modes:
 *  - Direct replacement via value
 *  - In-place mutation or transformation via function
 *
 * @param <T> the type of the array elements
 */
public class GenericEagerSegmentTree<T> implements SegmentTree<T> {

    private final T[] tree;
    private final T[] input;
    private final int n;
    private final BinaryOperator<T> operation;
    private final T defaultValue;

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

    public GenericEagerSegmentTree(List<T> inputList, BinaryOperator<T> operation, T defaultValue) {
        this(
                inputList == null ? null : inputList.toArray((T[]) new Object[0]),
                operation,
                defaultValue
        );
    }

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
     * Directly updates the value at a given index.
     *
     * @param index the index to update
     * @param value the new value to set
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @Override
    public void update(int index, T value) {
        if (index < 0 || index >= n)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        updateUtil(0, n - 1, index, value, 0);
    }

    /**
     * Updates the value at a given index using a transformation function.
     * The function receives the current value at the index and must return the updated value.
     *
     * @param index the index to update
     * @param updater a function that transforms the existing value
     * @throws IndexOutOfBoundsException if index is invalid
     */
    @Override
    public void update(int index, Function<T, T> updater) {
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

    @Override
    public int size() {
        return n;
    }

    public void printTree() {
        System.out.println("Segment Tree: " + Arrays.toString(tree));
    }
}
