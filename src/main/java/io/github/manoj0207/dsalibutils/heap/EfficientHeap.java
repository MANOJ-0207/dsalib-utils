package io.github.manoj0207.dsalibutils.heap;

import java.util.*;

/**
 * A highly flexible heap (priority queue) implementation that supports:
 * <ul>
 *     <li>Both min-heap and max-heap behavior via a comparator</li>
 *     <li>Efficient value removal in <code>O(log n)</code> using an index map</li>
 *     <li>Update operations (remove + reinsert) for arbitrary values</li>
 * </ul>
 * Internally uses:
 * <ul>
 *     <li>An ArrayList for heap structure</li>
 *     <li>A map from value to set of indices for <code>O(1)</code> deletion support</li>
 * </ul>
 *
 * @param <T> the type of elements in the heap; must be Comparable
 */
public class EfficientHeap<T extends Comparable<T>> {
    private final List<T> heap = new ArrayList<>();
    private final Map<T, Set<Integer>> valueToIndices = new HashMap<>();
    private final Comparator<T> comparator;

    /**
     * Constructs an EfficientHeap with either min-heap or max-heap ordering.
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
     */
    public EfficientHeap() {
        this(Comparator.naturalOrder());
    }

    /**
     * Checks if the heap is empty.
     *
     * @return true if heap is empty, false otherwise
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Returns the top element of the heap without removing it.
     *
     * @return the root element
     * @throws NoSuchElementException if the heap is empty
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return heap.get(0);
    }

    /**
     * Adds a value to the heap.
     *
     * @param val the value to insert
     * <p><b>Time Complexity:</b> O(log n)</p>
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
     * @throws NoSuchElementException if heap is empty
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public T poll() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T root = heap.get(0);
        remove(root);
        return root;
    }

    /**
     * Removes a specific value from the heap.
     * If multiple copies exist, removes only one.
     *
     * @param val the value to remove
     * @return true if the value was removed; false if not found
     * <p><b>Time Complexity:</b> O(log n)</p>
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
            heapifyUp(index); // Re-heapify if necessary
        } else {
            heap.remove(lastIndex);
        }

        return true;
    }

    /**
     * Updates a value by removing the old value and inserting the new one.
     *
     * @param oldVal the value to be replaced
     * @param newVal the value to be added
     * @return true if the value was updated; false if not found
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public boolean update(T oldVal, T newVal) {
        if (!remove(oldVal)) return false;
        add(newVal);
        return true;
    }

    /**
     * Prints the current heap elements in array form (for debugging).
     * <p><b>Time Complexity:</b> O(n)</p>
     */
    public void printHeap() {
        System.out.println(heap);
    }

    // ---------- Private Helper Methods (excluded from JavaDoc as per user request) ---------- //

    private void heapifyUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (comparator.compare(heap.get(i), heap.get(parent)) >= 0) break;
            swap(i, parent);
            i = parent;
        }
    }

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

    private void swap(int i, int j) {
        T valI = heap.get(i), valJ = heap.get(j);
        heap.set(i, valJ);
        heap.set(j, valI);
        updateIndexMapAfterSwap(valI, i, j);
        updateIndexMapAfterSwap(valJ, j, i);
    }

    private void updateIndexMapAfterSwap(T val, int oldIdx, int newIdx) {
        Set<Integer> indices = valueToIndices.get(val);
        if (indices != null) {
            indices.remove(oldIdx);
            indices.add(newIdx);
        }
    }
}
