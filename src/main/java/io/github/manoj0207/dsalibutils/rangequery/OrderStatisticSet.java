package io.github.manoj0207.dsalibutils.rangequery;

import io.github.manoj0207.dsalibutils.rangequery.segmenttree.GenericEagerSegmentTree;

import java.util.*;

/**
 * A generic order-statistics multiset data structure over a predefined, fixed universe of values.
 *
 * @param <T> the type of elements stored, must be Comparable
 */
public class OrderStatisticSet<T extends Comparable<T>> {

    private final int maxIndex;
    private final int[] freq;
    private final GenericEagerSegmentTree<Integer> seg;
    private final Map<T, Integer> valueToIndex;
    private final List<T> indexToValue;

    /**
     * Constructs an OrderStatisticSet over a predefined universe of values.
     *
     * @param universe a non-null, non-empty set of valid values (must be unique)
     * @throws IllegalArgumentException if the universe is null or empty
     */
    public OrderStatisticSet(Set<T> universe) {
        if (universe == null || universe.isEmpty()) {
            throw new IllegalArgumentException("Universe must be non-null and non-empty.");
        }

        List<T> sorted = new ArrayList<>(universe);
        Collections.sort(sorted);
        this.indexToValue = Collections.unmodifiableList(sorted); // prevent mutation

        this.valueToIndex = new HashMap<>();
        for (int i = 0; i < sorted.size(); i++) {
            valueToIndex.put(sorted.get(i), i);
        }

        this.maxIndex = sorted.size();
        this.freq = new int[maxIndex];

        this.seg = new GenericEagerSegmentTree<>(
                Arrays.stream(freq).boxed().toArray(Integer[]::new),
                Integer::sum,
                0
        );
    }

    /**
     * Constructs an OrderStatisticSet from a list of values and initializes the multiset.
     *
     * @param values list of elements to insert (can contain duplicates)
     * @throws IllegalArgumentException if values is null or empty
     */
    public OrderStatisticSet(List<T> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Input list must be non-null and non-empty.");
        }

        Set<T> universeSet = new HashSet<>(values);
        List<T> sorted = new ArrayList<>(universeSet);
        Collections.sort(sorted);
        this.indexToValue = Collections.unmodifiableList(sorted);

        this.valueToIndex = new HashMap<>();
        for (int i = 0; i < sorted.size(); i++) {
            valueToIndex.put(sorted.get(i), i);
        }

        this.maxIndex = sorted.size();
        this.freq = new int[maxIndex];

        this.seg = new GenericEagerSegmentTree<>(
                Arrays.stream(freq).boxed().toArray(Integer[]::new),
                Integer::sum,
                0
        );

        for (T val : values) {
            insert(val);
        }
    }

    /**
     * Inserts a value into the multiset.
     *
     * @param val the value to insert
     */
    public void insert(T val) {
        Integer idx = valueToIndex.get(val);
        if (idx == null) return;
        freq[idx]++;
        seg.update(idx, freq[idx]);
    }

    /**
     * Removes one occurrence of a value from the multiset.
     *
     * @param val the value to remove
     */
    public void remove(T val) {
        Integer idx = valueToIndex.get(val);
        if (idx == null || freq[idx] == 0) return;
        freq[idx]--;
        seg.update(idx, freq[idx]);
    }

    /**
     * Returns the number of times a value appears in the multiset.
     *
     * @param val the value to count
     * @return number of occurrences; 0 if not present or not in universe
     */
    public int count(T val) {
        Integer idx = valueToIndex.get(val);
        return idx == null ? 0 : freq[idx];
    }

    /**
     * Returns the number of elements strictly less than the given value.
     *
     * @param val the threshold value
     * @return number of elements less than {@code val}
     */
    public int countLessThan(T val) {
        Integer idx = getIndexBefore(val);
        if (idx == null || idx < 0) return 0;
        return seg.query(0, idx);
    }

    /**
     * Returns the k-th smallest value in the multiset (1-based indexing).
     *
     * @param k the rank (1-indexed)
     * @return the value if found, or null if k is out of bounds
     */
    public T kthSmallest(int k) {
        if (k <= 0 || seg.query(0, maxIndex - 1) < k) return null;

        int low = 0, high = maxIndex - 1, ans = -1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int count = seg.query(0, mid);
            if (count >= k) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans == -1 ? null : indexToValue.get(ans);
    }

    /**
     * Returns the total number of elements in the multiset (including duplicates).
     *
     * @return the total size
     */
    public int size() {
        return seg.query(0, maxIndex - 1);
    }

    /**
     * Checks if the multiset contains at least one occurrence of the value.
     *
     * @param val the value to check
     * @return true if present, false otherwise
     */
    public boolean contains(T val) {
        return count(val) > 0;
    }

    /**
     * Helper method to get the compressed index of the largest element < val.
     *
     * @param val reference value
     * @return index of predecessor in compressed array, or null if not found
     */
    private Integer getIndexBefore(T val) {
        int idx = Collections.binarySearch(indexToValue, val);
        if (idx >= 0) return idx - 1;
        int insertPoint = -idx - 1;
        return (insertPoint > 0) ? (insertPoint - 1) : null;
    }
}
