package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;

/**
 * <p>
 * Represents a weighted <b>directed edge</b> from {@code source} to {@code dest}
 * with a specified {@code weight}.
 * </p>
 *
 * <p>
 * This class is ideal for graph algorithms such as:
 * <ul>
 *   <li><b>Dijkstra's Algorithm</b></li>
 *   <li><b>Bellman-Ford</b></li>
 *   <li><b>Kruskal's MST</b></li>
 *   <li><b>Prim's MST</b> (though it prefers undirected edges)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Being a {@code record}, it provides:
 * <ul>
 *   <li>Immutable data structure</li>
 *   <li>Thread-safety guarantees</li>
 *   <li>Auto-generated {@code equals()}, {@code hashCode()}, and {@code toString()}</li>
 * </ul>
 * </p>
 *
 * @param <K>     the type of the graph nodes
 * @param source  the starting node of the edge
 * @param dest    the ending node of the edge
 * @param weight  the weight associated with the edge
 */
public record DetailedEdge<K>(K source, K dest, int weight) {

    /**
     * Returns a human-readable representation of the directed edge with its weight.
     *
     * @return a string in the format: {@code source --weight→ dest}
     */
    @Override
    public String toString() {
        return source + " --" + weight + "-→ " + dest;
    }
}
