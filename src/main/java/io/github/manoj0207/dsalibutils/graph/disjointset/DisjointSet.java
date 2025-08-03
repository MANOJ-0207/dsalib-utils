package io.github.manoj0207.dsalibutils.graph.disjointset;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic implementation of the Disjoint Set Union (Union-Find) data structure.
 * <p>
 * Supports:
 * <ul>
 *   <li>Union by rank</li>
 *   <li>Path compression</li>
 * </ul>
 * for near constant-time operations.
 *
 * @param <K> the type of the elements in the disjoint set
 */
public class DisjointSet<K> {

    /**
     * Maps each element to its parent.
     */
    private final Map<K, K> parent = new HashMap<>();

    /**
     * Stores the rank (approximate tree height) of each root node.
     */
    private final Map<K, Integer> rank = new HashMap<>();

    /**
     * Constructs an empty disjoint set.
     * Elements must be added using {@link #makeSet(Object)} or {@link #find(Object)}.
     */
    public DisjointSet() {}

    /**
     * Initializes the disjoint set with the given iterable of nodes.
     *
     * @param nodes the initial elements to be added as individual sets
     *
     * <p><b>Time Complexity:</b> O(n)</p>
     */
    public DisjointSet(Iterable<K> nodes) {
        for (K node : nodes) {
            makeSet(node);
        }
    }

    /**
     * Creates a new set containing the given element.
     * If the element already exists, this method does nothing.
     *
     * @param x the element to add
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public void makeSet(K x) {
        parent.putIfAbsent(x, x);
        rank.putIfAbsent(x, 0);
    }

    /**
     * Finds the representative (root) of the set that contains the given element.
     * Applies path compression for efficiency.
     * If the element doesn't exist, it will be added as its own set.
     *
     * @param x the element to find
     * @return the root representative of the set containing {@code x}
     *
     * <p><b>Time Complexity:</b> O(α(n)) — nearly constant using path compression</p>
     */
    public K find(K x) {
        if (!parent.containsKey(x)) {
            makeSet(x); // lazy initialization
        }

        K px = parent.get(x);
        if (!x.equals(px)) {
            parent.put(x, find(px)); // Path compression
        }

        return parent.get(x);
    }

    /**
     * Unites the sets that contain elements {@code x} and {@code y}.
     * Uses union by rank to optimize tree height.
     *
     * @param x one element
     * @param y the other element
     * @return {@code true} if the sets were different and have been merged; {@code false} if already in the same set
     *
     * <p><b>Time Complexity:</b> O(α(n)) — nearly constant</p>
     */
    public boolean union(K x, K y) {
        K rootX = find(x);
        K rootY = find(y);

        if (rootX.equals(rootY)) {
            return false; // Already in the same set
        }

        int rankX = rank.getOrDefault(rootX, 0);
        int rankY = rank.getOrDefault(rootY, 0);

        // Attach smaller tree under root of larger tree
        if (rankX < rankY) {
            parent.put(rootX, rootY);
        } else if (rankX > rankY) {
            parent.put(rootY, rootX);
        } else {
            parent.put(rootY, rootX);
            rank.put(rootX, rankX + 1); // Increase rank only when equal
        }

        return true;
    }

    /**
     * Checks whether two elements are in the same set.
     *
     * @param x one element
     * @param y the other element
     * @return {@code true} if both elements belong to the same set, {@code false} otherwise
     *
     * <p><b>Time Complexity:</b> O(α(n))</p>
     */
    public boolean isConnected(K x, K y) {
        return find(x).equals(find(y));
    }

    /**
     * Returns a shallow copy of the internal parent map.
     * Useful for debugging or visualization.
     *
     * @return a copy of the parent map
     *
     * <p><b>Time Complexity:</b> O(n)</p>
     */
    public Map<K, K> getParentMap() {
        return new HashMap<>(parent);
    }
}
