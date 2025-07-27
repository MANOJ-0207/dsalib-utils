package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;

/**
 * Represents a weighted edge pointing to a target node {@code node} with a specific {@code weight}.
 *
 * <p>This is commonly used in adjacency lists for weighted graphs, especially in
 * algorithms like Dijkstra, Prim, etc., where only the neighbor and edge weight are needed.</p>
 *
 * <p>As a record:
 * - This is immutable and thread-safe.
 * - Auto-generates {@code equals()}, {@code hashCode()}, and {@code toString()}.
 * - Implements {@code Comparable} by comparing edge weights.</p>
 *
 * @param node   the target node of the edge
 * @param weight the weight of the edge
 * @param <K>    the type of node
 */
public record WeightedEdge<K>(K node, int weight) implements Comparable<WeightedEdge<K>> {

    /**
     * Compares this edge with another based on weight (for use in priority queues).
     */
    @Override
    public int compareTo(WeightedEdge<K> other) {
        return Integer.compare(this.weight, other.weight);
    }

    /**
     * String representation of the edge for debugging/logging.
     */
    @Override
    public String toString() {
        return node + " (wt: " + weight + ")";
    }

    /**
     * Custom equality: edges are considered equal if they point to the same node (ignores weight).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WeightedEdge<?> edge)) return false;
        return node.equals(edge.node);
    }

    /**
     * Hash code based only on the target node (consistent with equals).
     */
    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
