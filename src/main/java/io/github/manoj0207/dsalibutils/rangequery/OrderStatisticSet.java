package io.github.manoj0207.dsalibutils.rangequery;

import io.github.manoj0207.dsalibutils.rangequery.segmenttree.GenericEagerSegmentTree;

import java.util.*;

/**
 * A generic order-statistics multiset data structure over a predefined, fixed universe of values.
 *
 * <p>This data structure allows insertion, removal, frequency counting, and k-th smallest element
 * queries in <b>O(log n)</b> time using a segment tree over a coordinate-compressed domain.</p>
 *
 * @param <T> the type of elements stored, must implement {@code Comparable}
 */
public class OrderStatisticSet<T extends Comparable<T>> {

    private int maxIndex;
    private int[] freq;
    private GenericEagerSegmentTree<Integer> seg;
    private Map<T, Integer> valueToIndex;
    private List<T> indexToValue;

    /**
     * Constructs a new {@code OrderStatisticSet} over a fixed universe with no inserted elements.
     *
     * @param universe a non-null, non-empty set of valid values
     * @throws IllegalArgumentException if {@code universe} is null or empty
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p> for sorting and initialization
     */
    public OrderStatisticSet(Set<T> universe) {
        this(universe, false);
    }

    /**
     * Constructs a new {@code OrderStatisticSet} over a fixed universe and optionally inserts them.
     *
     * @param universe a non-null, non-empty set of valid values
     * @param include  if {@code true}, inserts all elements from {@code universe} (acts like init-freq=1)
     * @throws IllegalArgumentException if {@code universe} is null or empty
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p>
     */
    public OrderStatisticSet(Set<T> universe, boolean include) {
        if (universe == null || universe.isEmpty()) {
            throw new IllegalArgumentException("Universe must be non-null and non-empty.");
        }

        List<T> list = new ArrayList<>(universe);
        Collections.sort(list);
        initialize(list, include ? list : Collections.emptyList());
    }

    /**
     * Constructs a new {@code OrderStatisticSet} from a list of values (no elements are inserted).
     * The universe will be built from the unique elements of the list.
     *
     * @param values a list of values used to define the universe
     * @throws IllegalArgumentException if {@code values} is null or empty
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p>
     */
    public OrderStatisticSet(List<T> values) {
        this(values, false);
    }

    /**
     * Constructs a new {@code OrderStatisticSet} from a list and optionally inserts them.
     *
     * @param values  a list of values (duplicates allowed) to define and populate the multiset
     * @param include if {@code true}, inserts all elements from {@code values}
     * @throws IllegalArgumentException if {@code values} is null or empty
     *
     * <p><b>Time Complexity:</b> <p>O(n log n)</p>
     */
    public OrderStatisticSet(List<T> values, boolean include) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Input list must be non-null and non-empty.");
        }

        Set<T> universeSet = new HashSet<>(values);
        List<T> sorted = new ArrayList<>(universeSet);
        Collections.sort(sorted);
        initialize(sorted, include ? values : Collections.emptyList());
    }

    // Internal setup: compress values, build tree and optionally insert
    private void initialize(List<T> sortedUniverse, List<T> initialValues) {
        this.indexToValue = Collections.unmodifiableList(sortedUniverse);
        this.valueToIndex = new HashMap<>();

        for (int i = 0; i < sortedUniverse.size(); i++) {
            valueToIndex.put(sortedUniverse.get(i), i);
        }

        this.maxIndex = sortedUniverse.size();
        this.freq = new int[maxIndex];

        this.seg = new GenericEagerSegmentTree<>(
                Arrays.stream(freq).boxed().toArray(Integer[]::new),
                Integer::sum,
                0
        );

        for (T val : initialValues) {
            insert(val);
        }
    }

    /**
     * Inserts one occurrence of a value into the multiset.
     *
     * @param val the value to insert (must exist in the universe)
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     */
    public void insert(T val) {
        Integer idx = valueToIndex.get(val);
        if (idx == null) return;
        freq[idx]++;
        seg.update(idx, freq[idx]);
    }

    /**
     * Removes one occurrence of the value from the multiset, if present.
     *
     * @param val the value to remove
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     */
    public void erase(T val) {
        Integer idx = valueToIndex.get(val);
        if (idx == null || freq[idx] == 0) return;
        freq[idx]--;
        seg.update(idx, freq[idx]);
    }

    /**
     * Returns the frequency of a value in the multiset.
     *
     * @param val the value to check
     * @return the number of times {@code val} occurs; 0 if not present
     *
     * <p><b>Time Complexity:</b> <p>O(1)</p>
     */
    public int count(T val) {
        Integer idx = valueToIndex.get(val);
        return idx == null ? 0 : freq[idx];
    }

    /**
     * Returns the number of elements strictly less than the given value.
     *
     * @param val the upper bound value (exclusive)
     * @return the number of elements less than {@code val}
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     */
    public int orderOfKey(T val) {
        Integer idx = getIndexBefore(val);
        if (idx == null || idx < 0) return 0;
        return seg.query(0, idx);
    }

    /**
     * Finds the k-th smallest element in the multiset.
     *
     * @param k the 1-based index of the element to retrieve
     * @return the k-th smallest element, or {@code null} if {@code k} is invalid
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     */
    public T findByOrder(int k) {
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
     * @return the total number of inserted elements
     *
     * <p><b>Time Complexity:</b> <p>O(1)</p>
     */
    public int size() {
        return seg.query(0, maxIndex - 1);
    }

    /**
     * Checks whether the multiset contains at least one occurrence of the value.
     *
     * @param val the value to check
     * @return {@code true} if the value is present, {@code false} otherwise
     *
     * <p><b>Time Complexity:</b> <p>O(1)</p>
     */
    public boolean contains(T val) {
        return count(val) > 0;
    }

    /**
     * Gets the index of the greatest element less than {@code val} in the compressed array.
     *
     * @param val the value to find the predecessor for
     * @return index of predecessor, or {@code null} if none exists
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     */
    private Integer getIndexBefore(T val) {
        int idx = Collections.binarySearch(indexToValue, val);
        if (idx >= 0) return idx - 1;
        int insertPoint = -idx - 1;
        return (insertPoint > 0) ? (insertPoint - 1) : null;
    }
}
