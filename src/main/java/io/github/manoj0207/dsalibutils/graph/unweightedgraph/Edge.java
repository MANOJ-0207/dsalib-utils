package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.Objects;

/**
 * A generic, immutable representation of an undirected edge between two nodes.
 *
 * <p>This record treats edges (u, v) and (v, u) as equal, making it suitable for undirected graphs.
 *
 * @param u one endpoint of the edge
 * @param v the other endpoint of the edge
 * @param <K> the type of node (vertex)
 */
public record Edge<K>(K u, K v) {

    /**
     * Overrides equals to ensure (u, v) and (v, u) are considered the same
     * for undirected graphs.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge<?> other)) return false;
        // Check for undirected equality: (u,v) == (v,u)
        return (Objects.equals(this.u, other.u) && Objects.equals(this.v, other.v))
                || (Objects.equals(this.u, other.v) && Objects.equals(this.v, other.u));
    }

    /**
     * Order-independent hashCode for use in hash-based collections like HashSet.
     */
    @Override
    public int hashCode() {
        // Makes hash code the same for (u,v) and (v,u)
        return Objects.hashCode(u) + Objects.hashCode(v);
    }

    /**
     * Readable string representation of the edge.
     */
    @Override
    public String toString() {
        return "(" + u + " — " + v + ")";
    }
}
