package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;

/**
 * Represents a weighted directed edge from {@code source} to {@code dest} with a given {@code weight}.
 *
 * <p>This is an immutable data structure ideal for use in algorithms like:
 * Dijkstra, Bellman-Ford, Kruskal, Prim, etc.</p>
 *
 * <p>Since this is a record, it automatically provides:
 * - Proper {@code equals()} and {@code hashCode()}
 * - A concise {@code toString()} format
 * - Immutability and thread-safety guarantees</p>
 *
 * @param source the starting node of the edge
 * @param dest the ending node of the edge
 * @param weight the weight associated with the edge
 * @param <K> the type of the graph nodes
 */
public record DetailedEdge<K>(K source, K dest, int weight) {

    @Override
    public String toString() {
        return source + " --" + weight + "→ " + dest;
    }
}
