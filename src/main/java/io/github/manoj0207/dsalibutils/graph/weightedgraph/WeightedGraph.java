package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import io.github.manoj0207.dsalibutils.graph.unweightedgraph.Edge;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.DetailedEdge;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <p>
 * Represents a <b>weighted graph</b> with generic vertex type {@code K}.
 * </p>
 *
 * <p>
 * This interface supports both adjacency list and adjacency matrix graph implementations.
 * It includes common algorithms such as:
 * <ul>
 *   <li>Traversal (BFS, DFS)</li>
 *   <li>Shortest paths (Dijkstra, Bellman-Ford, Floyd-Warshall)</li>
 *   <li>Minimum Spanning Tree (Prim’s, Kruskal’s)</li>
 *   <li>Strongly Connected Components</li>
 * </ul>
 * </p>
 *
 * @param <K> the type of the nodes/vertices in the graph
 */
public interface WeightedGraph<K> {

    /**
     * Adds a weighted edge between two vertices.
     * <p>If the graph is undirected, an edge is added in both directions.</p>
     *
     * @param from   the source vertex
     * @param to     the destination vertex
     * @param weight the weight of the edge
     */
    void addEdge(K from, K to, int weight);

    /**
     * Removes the edge from {@code from} to {@code to}.
     * <p>For undirected graphs, the reverse edge is also removed.</p>
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
     * Returns all <b>strongly connected components (SCCs)</b> in the graph.
     *
     * @return a list of SCCs, each represented as a list of vertices
     * @throws UnsupportedOperationException if called on an undirected graph
     */
    List<List<K>> getStronglyConnectedComponents();

    /**
     * Checks whether the graph is <b>strongly connected</b>.
     * <p>A graph is strongly connected if every vertex is reachable from every other vertex.</p>
     *
     * @return {@code true} if strongly connected; {@code false} otherwise
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
     * Returns a map of each vertex to its SCC index.
     *
     * @return a map where keys are vertices and values are SCC identifiers
     * @throws UnsupportedOperationException if called on an undirected graph
     */
    Map<K, Integer> getSCCMap();

    /**
     * Determines whether a path exists from {@code from} to {@code to}.
     *
     * @param from the starting vertex
     * @param to   the target vertex
     * @return {@code true} if a path exists; {@code false} otherwise
     */
    boolean isReachable(K from, K to);

    /**
     * Computes the shortest path distance from {@code source} to {@code destination}
     * using Dijkstra’s algorithm.
     *
     * <p>Assumes all edge weights are non-negative.</p>
     *
     * @param source      the starting vertex
     * @param destination the target vertex
     * @return the shortest distance, or {@code -1} if unreachable
     */
    Integer dijkstra(K source, K destination);

    /**
     * Computes shortest distances from {@code source} to all other vertices
     * using the Bellman-Ford algorithm.
     *
     * <p>Supports negative edge weights.</p>
     *
     * @param source the starting vertex
     * @return a map of shortest distances from the source
     * @throws IllegalStateException if a negative-weight cycle is detected
     */
    Map<K, Integer> bellmanFord(K source);

    /**
     * Computes all-pairs shortest paths using the Floyd-Warshall algorithm.
     *
     * <p>Supports negative edge weights, but not negative-weight cycles.</p>
     *
     * @return a map where {@code dist.get(u).get(v)} is the shortest distance from {@code u} to {@code v}
     */
    Map<K, Map<K, Integer>> floydWarshall();

    /**
     * Computes the total weight of the <b>Minimum Spanning Tree (MST)</b>
     * using Prim’s algorithm.
     *
     * @param start the vertex to start Prim’s algorithm from
     * @return the total weight of the MST
     */
    Integer primsMST(K start);

    /**
     * Computes the total weight of the <b>Minimum Spanning Tree (MST)</b>
     * using Kruskal’s algorithm.
     *
     * @return the total weight of the MST
     */
    Integer kruskalMST();

    /**
     * Checks whether the edge between {@code u} and {@code v} is a bridge.
     * <p>
     * A bridge is an edge whose removal increases the number of connected components in the graph.
     *
     * @param u one endpoint of the edge
     * @param v the other endpoint of the edge
     * @return {@code true} if the edge is a bridge, {@code false} otherwise
     */
    boolean isBridgeEdge(K u, K v);

    /**
     * Checks whether the given node is a bridge node (articulation point).
     * <p>
     * A bridge node is a node whose removal increases the number of connected components in the graph.
     *
     * @param node the node to check
     * @return {@code true} if the node is a bridge node, {@code false} otherwise
     */
    boolean isBridgeNode(K node);

    /**
     * Returns the set of all bridge edges in the graph.
     * <p>
     * A bridge edge (or cut-edge) is one whose removal increases the number of connected components.
     *
     * @return a set containing all bridge edges in the graph
     */
    Set<DetailedEdge<K>> getBridgeEdges();

    /**
     * Returns the set of all articulation points (bridge nodes) in the graph.
     *
     * @return a set containing all bridge nodes
     */
    Set<K> getBridgeNodes();

    /**
     * Prints the graph structure including all vertices and edges.
     * <p>This is typically used for debugging or visualization purposes.</p>
     */
    void printGraph();
}
