package io.github.manoj0207.dsalibutils.rangequery.segmenttree;

import java.util.function.Function;

public interface SegmentTree<T> {

    /**
     * Queries the combined result in the range [left, right].
     *
     * @param left  the starting index (inclusive)
     * @param right the ending index (inclusive)
     * @return the combined result of the specified range
     * @throws IllegalArgumentException if indices are out of bounds or invalid
     */
    T query(int left, int right);

    /**
     * Updates the value at a specific index.
     *
     * @param index the index to update
     * @param value the new value
     * @throws IllegalArgumentException if index is out of bounds
     */
    void update(int index, T value);

    /**
     * Updates the value at a specific index by applying a transformation function.
     * This allows modifying the existing value in-place without replacing it entirely.
     *
     * @param index   the index to update (0-based)
     * @param updater a function that takes the current value and returns the updated value
     * @throws IllegalArgumentException   if the updater function is null
     * @throws IndexOutOfBoundsException  if the index is out of bounds
     * @throws RuntimeException           if the updater function throws an exception during application
     */
    void update(int index, Function<T, T> updater);


    /**
     * Returns the size of the segment tree (i.e., length of original input array).
     *
     * @return the number of elements in the input array
     */
    int size();
}

