package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.DetailedEdge;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a weighted graph with generic vertex type {@code K}.
 * Supports both directed and undirected graph implementations.
 * Includes traversal, path-finding, and MST (Minimum Spanning Tree) algorithms.
 *
 * @param <K> the type of the keys/vertices in the graph
 */
public interface WeightedGraph<K> {

    /**
     * Adds a weighted edge between two vertices.
     * If the graph is undirected, an edge is added in both directions.
     *
     * @param from   the source vertex
     * @param to     the destination vertex
     * @param weight the weight of the edge
     */
    void addEdge(K from, K to, int weight);

    /**
     * Removes the edge from {@code from} to {@code to}.
     * For undirected graphs, it removes the edge in both directions.
     *
     * @param from the source vertex
     * @param to   the destination vertex
     */
    void removeEdge(K from, K to);

    /**
     * Performs Breadth-First Search (BFS) traversal from the given start vertex.
     *
     * @param start  the starting vertex
     * @param action the action to perform on each visited vertex
     */
    void bfs(K start, Consumer<K> action);

    /**
     * Performs Depth-First Search (DFS) traversal from the given start vertex.
     *
     * @param start  the starting vertex
     * @param action the action to perform on each visited vertex
     */
    void dfs(K start, Consumer<K> action);

    /**
     * Finds all strongly connected components (SCCs) in the graph.
     * Supported only for directed graphs.
     *
     * @return a list of SCCs, each represented as a list of vertices
     * @throws UnsupportedOperationException if called on an undirected graph
     */
    List<List<K>> getStronglyConnectedComponents();

    /**
     * Checks whether the entire graph is strongly connected.
     * A graph is strongly connected if every vertex is reachable from every other vertex.
     *
     * @return true if strongly connected, false otherwise
     */
    boolean isStronglyConnected();

    /**
     * Returns the number of strongly connected components (SCCs) in the graph.
     *
     * @return the count of SCCs
     * @throws UnsupportedOperationException if called on an undirected graph
     */
    int getSCCCount();

    /**
     * Returns a mapping of each vertex to its strongly connected component ID.
     *
     * @return a map where each key is a vertex and the value is the SCC index it belongs to
     * @throws UnsupportedOperationException if called on an undirected graph
     */
    Map<K, Integer> getSCCMap();

    /**
     * Determines if there is a path from vertex {@code from} to vertex {@code to}.
     *
     * @param from the starting vertex
     * @param to   the target vertex
     * @return true if reachable, false otherwise
     */
    boolean isReachable(K from, K to);

    /**
     * Computes the shortest path distance from {@code source} to {@code destination}
     * using Dijkstra's algorithm. Assumes all weights are non-negative.
     *
     * @param source      the starting vertex
     * @param destination the target vertex
     * @return the shortest distance, or -1 if the destination is unreachable
     */
    int dijkstra(K source, K destination);

    /**
     * Computes the shortest distances from the source to all other vertices
     * using the Bellman-Ford algorithm. Supports negative edge weights.
     *
     * @param source the starting vertex
     * @return a map of shortest distances from the source to every vertex
     * @throws IllegalStateException if a negative-weight cycle is detected
     */
    Map<K, Integer> bellmanFord(K source);

    /**
     * Computes all-pairs shortest paths using the Floyd-Warshall algorithm.
     * Can handle negative edge weights but no negative-weight cycles.
     *
     * @return a map of shortest distances from every node to every other node
     */
    Map<K, Map<K, Integer>> floydWarshall();

    /**
     * Computes the total weight of the Minimum Spanning Tree (MST)
     * using Prim's algorithm starting from the specified vertex.
     *
     * @param start the starting vertex
     * @return the total weight of the MST
     */
    int primsMST(K start);

    /**
     * Computes the total weight of the Minimum Spanning Tree (MST)
     * using Kruskal's algorithm.
     *
     * @return the total weight of the MST
     */
    int kruskalMST();

    /**
     * Prints the graph structure, including vertices and edges.
     * Mainly used for debugging and visualization purposes.
     */
    void printGraph();
}
