package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.function.Consumer;

public interface SimpleGraph<K> {

    void addEdge(K from, K to);

    void removeEdge(K from, K to);

    void bfs(K source, Consumer<K> action);

    void dfs(K source, Consumer<K> action);

    List<K> getTopologicalSort();

    Map<K, Integer> shortestDistances(K source);

    boolean isReachable(K from, K to);

    boolean isConnected();

    boolean isBridgeEdge(K u, K v);

    boolean isBridgeNode(K node);

    Set<Edge<K>> getBridgeEdges();

    /**
     * Returns the set of all articulation points (bridge nodes) in the graph.
     *
     * @return a set containing all bridge nodes
     */
    Set<K> getBridgeNodes();

}
