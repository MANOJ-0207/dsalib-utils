package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a generic unweighted graph interface.
 * <p>
 * Provides basic graph operations such as traversal, reachability, and bridge detection.
 *
 * @param <K> the type of the node (vertex)
 */
public interface SimpleGraph<K> {

    /**
     * Adds an edge between the specified nodes.
     * If the nodes do not exist in the graph, they should be created.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException if either {@code from} or {@code to} is null
     */
    void addEdge(K from, K to);

    /**
     * Removes the edge between the specified nodes if it exists.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException if either {@code from} or {@code to} is null
     */
    void removeEdge(K from, K to);

    /**
     * Performs Breadth-First Search (BFS) starting from the given source node.
     *
     * @param source the starting node
     * @param action a {@code Consumer} to be applied to each visited node
     */
    void bfs(K source, Consumer<K> action);

    /**
     * Performs Depth-First Search (DFS) starting from the given source node.
     *
     * @param source the starting node
     * @param action a {@code Consumer} to be applied to each visited node
     */
    void dfs(K source, Consumer<K> action);

    /**
     * Returns a topological ordering of the nodes in the graph.
     * <p>
     * Only valid for Directed Acyclic Graphs (DAGs).
     *
     * @return a list of nodes in topological order
     * @throws UnsupportedOperationException if the graph is undirected
     */
    List<K> getTopologicalSort();

    /**
     * Returns the shortest distances from the given source node to all reachable nodes.
     * <p>
     * Uses unweighted BFS traversal.
     *
     * @param source the starting node
     * @return a map of nodes to their shortest distance from the source
     */
    Map<K, Integer> shortestDistances(K source);

    /**
     * Checks whether there is a path from the {@code from} node to the {@code to} node.
     *
     * @param from the source node
     * @param to   the target node
     * @return {@code true} if a path exists, {@code false} otherwise
     */
    boolean isReachable(K from, K to);

    /**
     * Checks whether the graph is fully connected.
     * <p>
     * For undirected graphs, checks if every node is reachable from any other node.
     * For directed graphs, checks if all nodes are reachable from the first added node.
     *
     * @return {@code true} if the graph is connected, {@code false} otherwise
     */
    boolean isConnected();

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
    Set<Edge<K>> getBridgeEdges();

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
