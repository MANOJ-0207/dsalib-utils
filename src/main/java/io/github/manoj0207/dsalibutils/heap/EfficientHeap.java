package io.github.manoj0207.dsalibutils.heap;

import java.util.*;

/**
 * A highly flexible heap (priority queue) implementation that supports:
 * - Both min-heap and max-heap behavior via a comparator.
 * - Efficient value removal in O(log n) using an index map.
 * - Updates (removal + reinsertion) for arbitrary values.
 *
 * Internally uses:
 * - An ArrayList for the heap structure.
 * - A Map from value to its set of indices for O(1) lookup on deletion.
 *
 * @param <T> the type of elements maintained by this heap; must be Comparable
 */
public class EfficientHeap<T extends Comparable<T>> {
    private final List<T> heap = new ArrayList<>();
    private final Map<T, Set<Integer>> valueToIndices = new HashMap<>();
    private final Comparator<T> comparator;

    /**
     * Constructs an EfficientHeap with min-heap or max-heap ordering.
     *
     * @param isMinHeap true for min-heap (default), false for max-heap
     */
    public EfficientHeap(boolean isMinHeap) {
        this.comparator = isMinHeap ? Comparator.naturalOrder() : Comparator.reverseOrder();
    }

    /**
     * Constructs an EfficientHeap with a custom comparator.
     *
     * @param customComparator comparator defining heap ordering
     */
    public EfficientHeap(Comparator<T> customComparator) {
        this.comparator = customComparator;
    }

    /**
     * Constructs an EfficientHeap using natural ordering (min-heap).
     *
     * Requires that T implements Comparable<T>.
     */
    public EfficientHeap() {
        this(Comparator.naturalOrder());
    }

    /**
     * Checks if the heap is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Returns the top element of the heap without removing it.
     *
     * @return the root element
     * @throws NoSuchElementException if the heap is empty
     */
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return heap.get(0);
    }

    /**
     * Adds an element to the heap.
     *
     * @param val the value to be added
     */
    public void add(T val) {
        heap.add(val);
        int index = heap.size() - 1;
        valueToIndices.computeIfAbsent(val, k -> new HashSet<>()).add(index);
        heapifyUp(index);
    }

    /**
     * Removes and returns the root of the heap.
     *
     * @return the root element
     * @throws NoSuchElementException if the heap is empty
     */
    public T poll() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T root = heap.get(0);
        remove(root);
        return root;
    }

    /**
     * Removes a specific value from the heap.
     * If multiple occurrences exist, removes only one.
     *
     * @param val the value to remove
     * @return true if removed, false if not found
     */
    public boolean remove(T val) {
        if (!valueToIndices.containsKey(val)) return false;

        Iterator<Integer> it = valueToIndices.get(val).iterator();
        int index = it.next();
        it.remove();
        if (valueToIndices.get(val).isEmpty()) valueToIndices.remove(val);

        int lastIndex = heap.size() - 1;
        if (index != lastIndex) {
            T lastVal = heap.get(lastIndex);
            swap(index, lastIndex);
            heap.remove(lastIndex);
            updateIndexMapAfterSwap(lastVal, lastIndex, index);
            heapifyDown(index);
            heapifyUp(index); // Needed if lastVal is smaller than parent
        } else {
            heap.remove(lastIndex);
        }

        return true;
    }

    /**
     * Updates a value in the heap by removing the old value and adding the new one.
     *
     * @param oldVal the value to replace
     * @param newVal the value to insert
     * @return true if oldVal was found and updated, false otherwise
     */
    public boolean update(T oldVal, T newVal) {
        if (!remove(oldVal)) return false;
        add(newVal);
        return true;
    }

    // === Private Utility Methods === //

    /**
     * Maintains the heap property by bubbling up the element at index i.
     */
    private void heapifyUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (comparator.compare(heap.get(i), heap.get(parent)) >= 0) break;
            swap(i, parent);
            i = parent;
        }
    }

    /**
     * Maintains the heap property by pushing down the element at index i.
     */
    private void heapifyDown(int i) {
        int size = heap.size();
        while (true) {
            int left = 2 * i + 1, right = 2 * i + 2;
            int smallest = i;

            if (left < size && comparator.compare(heap.get(left), heap.get(smallest)) < 0)
                smallest = left;
            if (right < size && comparator.compare(heap.get(right), heap.get(smallest)) < 0)
                smallest = right;

            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    /**
     * Swaps elements at two indices and updates the index map accordingly.
     */
    private void swap(int i, int j) {
        T valI = heap.get(i), valJ = heap.get(j);
        heap.set(i, valJ);
        heap.set(j, valI);
        updateIndexMapAfterSwap(valI, i, j);
        updateIndexMapAfterSwap(valJ, j, i);
    }

    /**
     * Updates the value-to-indices map after a swap.
     *
     * @param val    the value that moved
     * @param oldIdx the old index
     * @param newIdx the new index
     */
    private void updateIndexMapAfterSwap(T val, int oldIdx, int newIdx) {
        Set<Integer> indices = valueToIndices.get(val);
        if (indices != null) {
            indices.remove(oldIdx);
            indices.add(newIdx);
        }
    }

    /**
     * Prints the current heap (useful for debugging).
     */
    public void printHeap() {
        System.out.println(heap);
    }
}
