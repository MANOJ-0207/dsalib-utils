package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * A generic segment tree supporting lazy propagation for efficient range updates and queries.
 *
 * <p>This implementation supports:
 * <ul>
 *   <li>Point update with direct object replacement</li>
 *   <li>Point update using transformation function</li>
 *   <li>Range update using transformation functions (chained in order)</li>
 * </ul>
 *
 * <p><b>Time Complexities:</b>
 * <ul>
 *   <li>Build: O(n)</li>
 *   <li>Query: O(log n)</li>
 *   <li>Point Update: O(log n)</li>
 *   <li>Range Update: O(log n)</li>
 * </ul>
 *
 * @param <T> the type of elements stored and operated on in the segment tree
 */
public class GenericLazySegmentTree<T> implements SegmentTree<T> {

    private final T[] tree;
    private final T[] input;
    private final Function<T, T>[] lazy;
    private final int n;

    private final BinaryOperator<T> combine;
    private final T defaultValue;

    /**
     * Constructs a lazy segment tree from an array.
     *
     * @param input        the input array (must not be null or empty)
     * @param combine      a binary operator to combine two elements (e.g. sum, min, max)
     * @param defaultValue the identity value for the query operation
     * @throws IllegalArgumentException if input is null/empty or combine is null
     */
    @SuppressWarnings("unchecked")
    public GenericLazySegmentTree(
            T[] input,
            BinaryOperator<T> combine,
            T defaultValue
    ) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("Input array must not be null or empty.");
        }
        if (combine == null) {
            throw new IllegalArgumentException("Combine function must not be null.");
        }

        this.n = input.length;
        this.input = Arrays.copyOf(input, n);
        this.combine = combine;
        this.defaultValue = defaultValue;

        int height = (int) Math.ceil(Math.log(n) / Math.log(2));
        int size = 2 * (1 << height) - 1;
        this.tree = (T[]) new Object[size];
        this.lazy = (Function<T, T>[]) new Function[size];

        Arrays.fill(tree, defaultValue);
        Arrays.fill(lazy, null);

        build(0, n - 1, 0);
    }

    /**
     * Constructs the tree from a list instead of an array.
     *
     * @param inputList    input data list (must not be null or empty)
     * @param combine      binary operation to combine results
     * @param defaultValue identity/default value for queries
     */
    public GenericLazySegmentTree(List<T> inputList, BinaryOperator<T> combine, T defaultValue) {
        this((inputList == null) ? null : inputList.toArray((T[]) new Object[0]), combine, defaultValue);
    }

    // --------------------- INTERNAL METHODS ----------------------

    /**
     * Recursively builds the segment tree from the input array.
     */
    private void build(int start, int end, int node) {
        if (start == end) {
            tree[node] = input[start];
        } else {
            int mid = (start + end) >>> 1;
            build(start, mid, 2 * node + 1);
            build(mid + 1, end, 2 * node + 2);
            tree[node] = combine.apply(tree[2 * node + 1], tree[2 * node + 2]);
        }
    }

    /**
     * Applies any pending lazy operations at the current node.
     */
    private void propagate(int start, int end, int node) {
        if (lazy[node] != null) {
            tree[node] = lazy[node].apply(tree[node]);

            if (start != end) {
                lazy[2 * node + 1] = chainFunctions(lazy[2 * node + 1], lazy[node]);
                lazy[2 * node + 2] = chainFunctions(lazy[2 * node + 2], lazy[node]);
            }

            lazy[node] = null;
        }
    }

    /**
     * Chains two transformation functions in order: f1 ∘ f2
     */
    private Function<T, T> chainFunctions(Function<T, T> f1, Function<T, T> f2) {
        if (f1 == null) return f2;
        if (f2 == null) return f1;
        return f1.andThen(f2);
    }

    /**
     * Updates the value at a specific index with a new object.
     *
     * @param index the index to update
     * @param value the new value to replace with
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    @Override
    public void update(int index, T value) {
        if (index < 0 || index >= n)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        updatePoint(0, n - 1, index, value, 0);
    }

    private void updatePoint(int start, int end, int index, T value, int node) {
        propagate(start, end, node);

        if (start == end) {
            tree[node] = value;
            return;
        }

        int mid = (start + end) >>> 1;
        if (index <= mid)
            updatePoint(start, mid, index, value, 2 * node + 1);
        else
            updatePoint(mid + 1, end, index, value, 2 * node + 2);

        tree[node] = combine.apply(tree[2 * node + 1], tree[2 * node + 2]);
    }

    /**
     * Updates the value at a specific index by applying a transformation function.
     *
     * @param index  the index to update
     * @param updater the function to apply to the current value
     * @throws IllegalArgumentException if the updater function is null
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public void update(int index, Function<T, T> updater) {
        if (index < 0 || index >= n)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        if (updater == null)
            throw new IllegalArgumentException("Update function must not be null.");

        T oldValue = query(index, index);
        T newValue = updater.apply(oldValue);
        update(index, newValue);
    }

    /**
     * Applies a transformation function lazily to all elements in the range [l, r].
     *
     * @param l    the left index (inclusive)
     * @param r    the right index (inclusive)
     * @param func the function to apply
     * @throws IllegalArgumentException if range is invalid or function is null
     */
    public void update(int l, int r, Function<T, T> func) {
        if (l < 0 || r >= n || l > r)
            throw new IllegalArgumentException("Invalid update range: [" + l + ", " + r + "]");
        if (func == null)
            throw new IllegalArgumentException("Update function must not be null.");
        updateRange(0, n - 1, l, r, 0, func);
    }

    private void updateRange(int start, int end, int l, int r, int node, Function<T, T> func) {
        propagate(start, end, node);
        if (start > r || end < l) return;

        if (start >= l && end <= r) {
            lazy[node] = chainFunctions(lazy[node], func);
            propagate(start, end, node);
            return;
        }

        int mid = (start + end) >>> 1;
        updateRange(start, mid, l, r, 2 * node + 1, func);
        updateRange(mid + 1, end, l, r, 2 * node + 2, func);

        tree[node] = combine.apply(tree[2 * node + 1], tree[2 * node + 2]);
    }

    /**
     * Queries the result over a range [l, r] after applying all pending updates.
     *
     * @param l the left index (inclusive)
     * @param r the right index (inclusive)
     * @return the result of combining all values in the range
     * @throws IllegalArgumentException if range is invalid
     */
    @Override
    public T query(int l, int r) {
        if (l < 0 || r >= n || l > r)
            throw new IllegalArgumentException("Invalid query range: [" + l + ", " + r + "]");
        return query(0, n - 1, l, r, 0);
    }

    private T query(int start, int end, int l, int r, int node) {
        propagate(start, end, node);
        if (start > r || end < l) return defaultValue;

        if (start >= l && end <= r) return tree[node];

        int mid = (start + end) >>> 1;
        T left = query(start, mid, l, r, 2 * node + 1);
        T right = query(mid + 1, end, l, r, 2 * node + 2);
        return combine.apply(left, right);
    }

    /**
     * Returns the number of elements managed by the segment tree.
     *
     * @return size of the original input array
     */
    @Override
    public int size() {
        return n;
    }

    /**
     * Prints the internal tree structure for debugging purposes.
     */
    public void printTree() {
        System.out.println("Segment Tree: " + Arrays.toString(tree));
    }
}
