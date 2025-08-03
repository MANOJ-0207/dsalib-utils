package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.Objects;

/**
 * A generic, immutable representation of an edge between two nodes.
 * <p>
 * This record supports both directed and undirected edges.
 * For undirected edges, (u, v) is considered equal to (v, u).
 * For directed edges, (u, v) is only equal to (v, u) if both source and destination match exactly.
 *
 * @param <K> the type of node (vertex)
 * @param u   the source node (or one endpoint in undirected graph)
 * @param v   the destination node (or the other endpoint in undirected graph)
 * @param isDirected whether this edge is directed (true) or undirected (false)
 */
public record Edge<K>(K u, K v, boolean isDirected) {

    /**
     * Checks if this edge is equal to another object.
     * <p>
     * For directed graphs, equality requires both direction and endpoints to match:
     * (u → v) == (u → v) only.
     * <br>
     * For undirected graphs, edges are considered equal regardless of endpoint order:
     * (u — v) == (v — u).
     *
     * @param o the object to compare with
     * @return {@code true} if the given object is an {@code Edge} with the same nodes
     *         and same directionality semantics, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge<?> other)) return false;
        if (this.isDirected != other.isDirected) return false;

        boolean directMatch = Objects.equals(u, other.u) && Objects.equals(v, other.v);
        return isDirected
                ? directMatch
                : directMatch || (Objects.equals(u, other.v) && Objects.equals(v, other.u));
    }

    /**
     * Computes a hash code for this edge.
     * <p>
     * For directed graphs, the hash is order-sensitive: hash(u, v).
     * For undirected graphs, the hash is order-insensitive: hash(u) + hash(v),
     * ensuring (u, v) and (v, u) yield the same hash.
     *
     * @return a hash code consistent with the equals() contract
     */
    @Override
    public int hashCode() {
        return isDirected
                ? Objects.hash(u, v)
                : Objects.hashCode(u) + Objects.hashCode(v);
    }

    /**
     * Returns a string representation of the edge.
     * <p>
     * Format:
     * <ul>
     *     <li>Directed: {@code (u → v)}</li>
     *     <li>Undirected: {@code (u — v)}</li>
     * </ul>
     *
     * @return a human-readable string of the edge
     */
    @Override
    public String toString() {
        return isDirected ? "(" + u + " → " + v + ")" : "(" + u + " — " + v + ")";
    }
}
