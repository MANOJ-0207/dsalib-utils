package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic graph implementation using an adjacency list.
 * Supports both directed and undirected graphs and includes
 * efficient methods for bridge edge/node detection with caching.
 *
 * @param <K> the type of the nodes (vertices)
 */
public class AdjListSimpleGraph<K> implements SimpleGraph<K>{
    private final Map<K, List<K>> adjacencyList = new HashMap<>();
    private final boolean isDirected;

    // Caching Tarjan results
    private boolean bridgeInfoValid = false;
    private Set<K> cachedArticulationPoints = new HashSet<>();
    private Set<Edge<K>> cachedBridgeEdges = new HashSet<>();

    // Tarjan variables
    private int time;
    private Map<K, Integer> tin, low;
    private Set<K> visitedTarjan;
    private List<Edge<K>> bridgeEdges;
    private Set<K> articulationPoints;

    public AdjListSimpleGraph() {
        this(false);
    }

    public AdjListSimpleGraph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    /**
     * Adds an edge between two nodes. In undirected graphs, adds both directions.
     * Invalidates Tarjan cache.
     *
     * @param from source node
     * @param to destination node
     */
    public void addEdge(K from, K to) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()); // Do NOT overwrite!
        if (!isDirected) {
            adjacencyList.get(to).add(from);
        }
        bridgeInfoValid = false; // Invalidate Tarjan cache
    }


    /**
     * Removes the edge from 'from' to 'to'. If the graph is undirected, removes both directions.
     * Does nothing if the edge or nodes do not exist.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException if either node is null
     */
    public void removeEdge(K from, K to) {
        Objects.requireNonNull(from, "From-node cannot be null");
        Objects.requireNonNull(to, "To-node cannot be null");

        List<K> fromList = adjacencyList.get(from);
        if (fromList != null) {
            fromList.remove(to);
        }

        if (!isDirected) {
            List<K> toList = adjacencyList.get(to);
            if (toList != null) {
                toList.remove(from);
            }
        }

        bridgeInfoValid = false; // Invalidate bridge cache
    }

    /**
     * Returns an unmodifiable view of the adjacency list.
     * Intended for testing or inspection purposes.
     */
    public Map<K, List<K>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    /**
     * Performs Breadth-First Search (BFS) from a source node.
     *
     * @param source starting node
     * @param action action to perform on each visited node
     */
    public void bfs(K source, Consumer<K> action) {
        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        visited.add(source);
        queue.offer(source);

        while (!queue.isEmpty()) {
            K node = queue.poll();
            action.accept(node);
            for (K neighbor : adjacencyList.getOrDefault(node, List.of())) {
                if (visited.add(neighbor)) {
                    queue.offer(neighbor);
                }
            }
        }
    }

    /**
     * Performs Depth-First Search (DFS) from a source node.
     *
     * @param source starting node
     * @param action action to perform on each visited node
     */
    public void dfs(K source, Consumer<K> action) {
        Set<K> visited = new HashSet<>();
        dfsHelper(source, visited, action);
    }

    private void dfsHelper(K node, Set<K> visited, Consumer<K> action) {
        visited.add(node);
        action.accept(node);

        for (K neighbor : adjacencyList.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, action);
            }
        }
    }

    /**
     * Returns a topological sort of the graph (only for directed graphs).
     *
     * @return list of nodes in topological order
     * @throws UnsupportedOperationException if graph is undirected
     */
    public List<K> getTopologicalSort() {
        if (!isDirected) {
            throw new UnsupportedOperationException("Topological sort is supported only in directed graphs.");
        }

        Set<K> visited = new HashSet<>();
        Deque<K> stack = new ArrayDeque<>();

        for (K node : adjacencyList.keySet()) {
            if (!visited.contains(node)) {
                topoDfs(node, visited, stack);
            }
        }

        List<K> result = new ArrayList<>(stack);
        return result;
    }

    private void topoDfs(K node, Set<K> visited, Deque<K> stack) {
        visited.add(node);
        for (K neighbor : adjacencyList.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                topoDfs(neighbor, visited, stack);
            }
        }
        stack.push(node);
    }

    /**
     * Returns shortest distances from a source node using BFS.
     *
     * @param source starting node
     * @return map of node to distance from source
     */
    public Map<K, Integer> shortestDistances(K source) {
        Map<K, Integer> distance = new HashMap<>();
        Queue<K> queue = new LinkedList<>();
        queue.add(source);
        distance.put(source, 0);

        while (!queue.isEmpty()) {
            K node = queue.poll();
            for (K neighbor : adjacencyList.getOrDefault(node, List.of())) {
                if (!distance.containsKey(neighbor)) {
                    distance.put(neighbor, distance.get(node) + 1);
                    queue.offer(neighbor);
                }
            }
        }

        return distance;
    }

    /**
     * Checks if a path exists between two nodes.
     *
     * @param from start node
     * @param to end node
     * @return true if reachable
     */
    public boolean isReachable(K from, K to) {
        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) return false;

        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        queue.offer(from);
        visited.add(from);

        while (!queue.isEmpty()) {
            K current = queue.poll();
            if (current.equals(to)) return true;

            for (K neighbor : adjacencyList.getOrDefault(current, List.of())) {
                if (visited.add(neighbor)) {
                    queue.offer(neighbor);
                }
            }
        }

        return false;
    }

    /**
     * Checks if the entire graph is connected (for undirected graph).
     *
     * @return true if connected
     */
    public boolean isConnected() {
        if (adjacencyList.isEmpty()) return true;
        Set<K> visited = new HashSet<>();
        dfsHelper(adjacencyList.keySet().iterator().next(), visited, node -> {});
        return visited.size() == adjacencyList.size();
    }

    /**
     * Returns true if the given edge is a bridge (critical connection).
     *
     * @param u first node
     * @param v second node
     * @return true if the edge is a bridge
     */
    public boolean isBridgeEdge(K u, K v) {
        computeTarjanIfNeeded();
        return cachedBridgeEdges.contains(new Edge<>(u, v));
    }

    /**
     * Returns true if the node is a bridge node (articulation point).
     *
     * @param node node to check
     * @return true if it's a bridge node
     */
    public boolean isBridgeNode(K node) {
        computeTarjanIfNeeded();
        return cachedArticulationPoints.contains(node);
    }

    /**
     * Returns the set of all bridge edges.
     *
     * @return set of bridge edges
     */
    public Set<Edge<K>> getBridgeEdges() {
        computeTarjanIfNeeded();
        return Collections.unmodifiableSet(cachedBridgeEdges);
    }

    /**
     * Returns the set of all articulation points (bridge nodes) in the graph.
     * A bridge node is a vertex whose removal increases the number of connected components.
     *
     * @return an unmodifiable set of all bridge nodes in the graph
     */
    @Override
    public Set<K> getBridgeNodes() {
        computeTarjanIfNeeded();
        return Collections.unmodifiableSet(cachedArticulationPoints);
    }


    private void computeTarjanIfNeeded() {
        if (bridgeInfoValid) return;

        time = 0;
        tin = new HashMap<>();
        low = new HashMap<>();
        visitedTarjan = new HashSet<>();
        bridgeEdges = new ArrayList<>();
        articulationPoints = new HashSet<>();

        for (K node : adjacencyList.keySet()) {
            if (!visitedTarjan.contains(node)) {
                dfsTarjan(node, null);
            }
        }

        cachedArticulationPoints = articulationPoints;
        cachedBridgeEdges = new HashSet<>(bridgeEdges);
        bridgeInfoValid = true;
    }

    private void dfsTarjan(K u, K parent) {
        visitedTarjan.add(u);
        tin.put(u, time);
        low.put(u, time++);
        int children = 0;

        for (K v : adjacencyList.getOrDefault(u, List.of())) {
            if (v.equals(parent)) continue;

            if (!visitedTarjan.contains(v)) {
                children++;
                dfsTarjan(v, u);
                low.put(u, Math.min(low.get(u), low.get(v)));

                if (parent != null && low.get(v) >= tin.get(u)) {
                    articulationPoints.add(u);
                }

                if (low.get(v) > tin.get(u)) {
                    bridgeEdges.add(new Edge<>(u, v));
                }

            } else {
                low.put(u, Math.min(low.get(u), tin.get(v)));
            }
        }

        if (parent == null && children > 1) {
            articulationPoints.add(u);
        }
    }

    /**
     * Prints the adjacency list.
     */
    public void printGraph() {
        for (Map.Entry<K, List<K>> entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }
}
