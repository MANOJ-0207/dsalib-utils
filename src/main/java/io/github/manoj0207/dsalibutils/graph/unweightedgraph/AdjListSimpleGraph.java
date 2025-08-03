package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic graph implementation using an adjacency list.
 * <p>
 * Supports both directed and undirected graphs, and includes
 * efficient methods for bridge edge/node detection using Tarjan's algorithm.
 *
 * @param <K> the type of the nodes (vertices)
 */
public class AdjListSimpleGraph<K> implements SimpleGraph<K> {
    private final Map<K, List<K>> adjacencyList = new HashMap<>();
    private final boolean isDirected;

    // Tarjan caching
    private boolean bridgeInfoValid = false;
    private Set<K> cachedArticulationPoints = new HashSet<>();
    private Set<Edge<K>> cachedBridgeEdges = new HashSet<>();

    // Tarjan variables
    private int time;
    private Map<K, Integer> tin, low;
    private Set<K> visitedTarjan;
    private List<Edge<K>> bridgeEdges;
    private Set<K> articulationPoints;

    /**
     * Constructs an undirected graph.
     */
    public AdjListSimpleGraph() {
        this(false);
    }

    /**
     * Constructs a graph with the specified directionality.
     *
     * @param isDirected true for directed, false for undirected
     */
    public AdjListSimpleGraph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    /**
     * Adds an edge between two nodes. In undirected graphs, adds both directions.
     * Invalidates Tarjan cache.
     *
     * @param from source node
     * @param to   destination node
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    @Override
    public void addEdge(K from, K to) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>());
        if (!isDirected) {
            adjacencyList.get(to).add(from);
        }
        bridgeInfoValid = false;
    }

    /**
     * Removes an edge from the graph. If undirected, removes both directions.
     *
     * @param from source node
     * @param to   destination node
     *
     * <p><b>Time Complexity:</b> O(deg(from) + deg(to))</p>
     */
    @Override
    public void removeEdge(K from, K to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        List<K> fromList = adjacencyList.get(from);
        if (fromList != null)
            fromList.remove(to);

        if (!isDirected) {
            List<K> toList = adjacencyList.get(to);
            if (toList != null) toList.remove(from);
        }

        bridgeInfoValid = false;
    }

    /**
     * Returns an unmodifiable view of the adjacency list.
     *
     * @return the graph's adjacency list
     */
    public Map<K, List<K>> getAdjacencyList() {
        return Collections.unmodifiableMap(adjacencyList);
    }

    /**
     * Performs Breadth-First Search from a source node.
     *
     * @param source the starting node
     * @param action action to perform on each visited node
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
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
     * Performs Depth-First Search from a source node.
     *
     * @param source the starting node
     * @param action action to perform on each visited node
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
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
     * Performs a topological sort of the graph.
     *
     * @return list of nodes in topological order
     * @throws UnsupportedOperationException if the graph is undirected
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
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

        return new ArrayList<>(stack);
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
     * Computes shortest distances from a source node using BFS.
     *
     * @param source the starting node
     * @return map of node to shortest distance
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
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
     * Checks whether a path exists from {@code from} to {@code to}.
     *
     * @param from start node
     * @param to   destination node
     * @return true if reachable
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
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
     * Checks if the undirected graph is fully connected.
     *
     * @return true if all nodes are reachable from any node
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public boolean isConnected() {
        if (adjacencyList.isEmpty()) return true;
        Set<K> visited = new HashSet<>();
        dfsHelper(adjacencyList.keySet().iterator().next(), visited, node -> {});
        return visited.size() == adjacencyList.size();
    }

    /**
     * Checks if an edge is a bridge (i.e., its removal increases components).
     *
     * @param u one end of the edge
     * @param v the other end of the edge
     * @return true if it is a bridge
     */
    @Override
    public boolean isBridgeEdge(K u, K v) {
        computeTarjanIfNeeded();
        return cachedBridgeEdges.contains(new Edge<>(u, v, isDirected));
    }

    /**
     * Checks if a node is an articulation point.
     *
     * @param node the node to check
     * @return true if it's a bridge node
     */
    @Override
    public boolean isBridgeNode(K node) {
        computeTarjanIfNeeded();
        return cachedArticulationPoints.contains(node);
    }

    /**
     * Returns all bridge edges in the graph.
     *
     * @return set of bridge edges
     */
    @Override
    public Set<Edge<K>> getBridgeEdges() {
        computeTarjanIfNeeded();
        return Collections.unmodifiableSet(cachedBridgeEdges);
    }

    /**
     * Returns all articulation points in the graph.
     *
     * @return set of bridge nodes
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
                    bridgeEdges.add(new Edge<>(u, v, isDirected));
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
     * Prints the adjacency list of the graph.
     */
    @Override
    public void printGraph() {
        for (Map.Entry<K, List<K>> entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }
}
