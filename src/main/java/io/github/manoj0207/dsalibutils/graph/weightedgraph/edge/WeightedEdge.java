package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;

/**
 * <p>
 * Represents a <b>weighted edge</b> pointing to a target node {@code node} with a specified {@code weight}.
 * </p>
 *
 * <p>
 * This structure is commonly used in adjacency lists of weighted graphs, especially in algorithms such as:
 * <ul>
 *   <li><b>Dijkstra's Algorithm</b></li>
 *   <li><b>Prim's Minimum Spanning Tree</b></li>
 *   <li>Any graph traversal where edge weights to neighbors are tracked</li>
 * </ul>
 * </p>
 *
 * <p>
 * Key characteristics:
 * <ul>
 *   <li><b>Immutable</b> and <b>thread-safe</b> as it is a Java {@code record}</li>
 *   <li>Auto-generated {@code equals()}, {@code hashCode()}, and {@code toString()}</li>
 *   <li><b>Custom equality</b>: Two edges are considered equal if they point to the same target node, regardless of weight</li>
 *   <li><b>Comparable</b>: Ordered by edge weight (ascending)</li>
 * </ul>
 * </p>
 *
 * @param <K>     the type of the node
 * @param node    the target node this edge points to
 * @param weight  the weight of the edge
 */
public record WeightedEdge<K>(K node, int weight) implements Comparable<WeightedEdge<K>> {

    /**
     * <p>
     * Compares this edge to another edge based on weight.
     * </p>
     *
     * <p>
     * This is useful for priority queues and greedy algorithms like Prim's and Dijkstra's.
     * </p>
     *
     * @param other the other edge to compare to
     * @return a negative value if this edge has lower weight, positive if higher, 0 if equal
     */
    @Override
    public int compareTo(WeightedEdge<K> other) {
        return Integer.compare(this.weight, other.weight);
    }

    /**
     * <p>
     * Returns a human-readable string representation of this edge.
     * </p>
     *
     * @return a string in the format: {@code targetNode (wt: weight)}
     */
    @Override
    public String toString() {
        return node + " (wt: " + weight + ")";
    }

    /**
     * <p>
     * Determines equality based only on the target node.
     * </p>
     *
     * <p>
     * This is useful when using {@code Set<WeightedEdge<K>>} or {@code Map<K, WeightedEdge<K>>}
     * to represent the best known edge to each node (e.g., in Prim's or Dijkstra's algorithms).
     * </p>
     *
     * @param obj the object to compare
     * @return {@code true} if the other object is a {@code WeightedEdge} with the same node, ignoring weight
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WeightedEdge<?> edge)) return false;
        return node.equals(edge.node);
    }

    /**
     * <p>
     * Computes hash code using only the target node.
     * </p>
     *
     * <p>
     * This ensures consistency with {@code equals()}, where weight is not considered.
     * </p>
     *
     * @return the hash code of the target node
     */
    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
