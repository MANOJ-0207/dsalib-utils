package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import java.util.function.Function;

/**
 * A generic interface for segment trees supporting efficient range queries and point/range updates.
 *
 * <p>This abstraction allows implementations that either use eager or lazy propagation strategies
 * depending on the update/query characteristics.</p>
 *
 * @param <T> the type of elements stored in the segment tree
 */
public interface SegmentTree<T> {

    /**
     * Queries the result over the inclusive range [left, right].
     * The result is computed by applying the segment tree's merge operation
     * on all values in the specified range.
     *
     * @param left  the starting index of the range (inclusive)
     * @param right the ending index of the range (inclusive)
     * @return the aggregated result over the specified range
     * @throws IndexOutOfBoundsException if {@code left < 0}, {@code right >= size()}, or {@code left > right}
     */
    T query(int left, int right);

    /**
     * Replaces the value at a specific index with the given value.
     *
     * @param index the index to update (0-based)
     * @param value the new value to set at the specified index
     * @throws IndexOutOfBoundsException if {@code index < 0} or {@code index >= size()}
     */
    void update(int index, T value);

    /**
     * Updates the value at the specified index using a transformation function.
     * This function receives the current value at the index and returns the modified value.
     *
     * @param index   the index to update (0-based)
     * @param updater a non-null function that maps the current value to a new value
     * @throws NullPointerException      if {@code updater} is {@code null}
     * @throws IndexOutOfBoundsException if {@code index < 0} or {@code index >= size()}
     */
    void update(int index, Function<T, T> updater);

    /**
     * Applies a transformation function to all elements in the inclusive range [l, r].
     * Each element in the range is updated individually by applying the provided function.
     *
     * @param l      the left bound of the range (inclusive)
     * @param r      the right bound of the range (inclusive)
     * @param updater a non-null function to apply to each element in the range
     * @throws NullPointerException       if {@code updater} is {@code null}
     * @throws IndexOutOfBoundsException  if {@code l < 0}, {@code r >= size()}, or {@code l > r}
     */
    void update(int l, int r, Function<T, T> updater);

    /**
     * Returns the total number of elements managed by the segment tree.
     *
     * @return the size of the input array used to construct the segment tree
     */
    int size();
}
