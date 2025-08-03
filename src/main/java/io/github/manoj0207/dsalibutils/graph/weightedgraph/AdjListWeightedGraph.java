package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import io.github.manoj0207.dsalibutils.graph.disjointset.DisjointSet;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.DetailedEdge;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic weighted graph using adjacency list.
 * Supports both directed and undirected graphs, various shortest path algorithms,
 * minimum spanning trees, and strongly connected component (SCC) analysis.
 *
 * Also supports bridge edge/node detection using Tarjan's algorithm (for undirected graphs).
 *
 * @param <K> the type of the nodes (vertices)
 */
public class AdjListWeightedGraph<K> implements WeightedGraph<K> {
    private final Map<K, List<WeightedEdge<K>>> adjacencyList = new HashMap<>();
    private final List<DetailedEdge<K>> allEdges = new ArrayList<>();
    private final boolean isDirected;

    // SCC (Kosaraju) cache
    private boolean sccCacheValid = false;
    private List<List<K>> cachedSCCs = new ArrayList<>();
    private final Map<K, Integer> cachedSccMap = new HashMap<>();

    // Tarjan cache (bridge detection) - Fixed implementation
    private boolean bridgeInfoValid = false;
    private Set<K> cachedArticulationPoints = new HashSet<>();
    private Set<DetailedEdge<K>> cachedBridgeEdges = new HashSet<>();

    // Tarjan variables
    private int time;
    private Map<K, Integer> tin, low;
    private Set<K> visitedTarjan;
    private List<DetailedEdge<K>> bridgeEdges;
    private Set<K> articulationPoints;

    public AdjListWeightedGraph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    /**
     * Adds an edge between two nodes with the specified weight.
     * In undirected graphs, adds both directions.
     * Invalidates all caches.
     *
     * @param from   source node
     * @param to     destination node
     * @param weight edge weight
     * @throws NullPointerException if from or to is null
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    @Override
    public void addEdge(K from, K to, int weight) {
        Objects.requireNonNull(from, "Source node cannot be null");
        Objects.requireNonNull(to, "Destination node cannot be null");

        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(new WeightedEdge<>(to, weight));
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>());

        if (!isDirected) {
            adjacencyList.get(to).add(new WeightedEdge<>(from, weight));
        }

        allEdges.add(new DetailedEdge<>(from, to, weight));
        invalidateCache();
    }

    /**
     * Removes an edge from the graph. If undirected, removes both directions.
     * Cleans up empty adjacency lists to prevent memory leaks.
     *
     * @param from source node
     * @param to   destination node
     * @throws NullPointerException if from or to is null
     *
     * <p><b>Time Complexity:</b> O(deg(from) + deg(to) + E)</p>
     */
    @Override
    public void removeEdge(K from, K to) {
        Objects.requireNonNull(from, "Source node cannot be null");
        Objects.requireNonNull(to, "Destination node cannot be null");

        List<WeightedEdge<K>> fromList = adjacencyList.get(from);
        if (fromList != null) {
            fromList.removeIf(e -> e.node().equals(to));
            // Clean up empty adjacency list to prevent memory leaks
            if (fromList.isEmpty()) {
                adjacencyList.remove(from);
            }
        }

        if (!isDirected) {
            List<WeightedEdge<K>> toList = adjacencyList.get(to);
            if (toList != null) {
                toList.removeIf(e -> e.node().equals(from));
                // Clean up empty adjacency list to prevent memory leaks
                if (toList.isEmpty()) {
                    adjacencyList.remove(to);
                }
            }
        }

        // Remove from allEdges list
        allEdges.removeIf(e -> (e.source().equals(from) && e.dest().equals(to)) ||
                (!isDirected && e.source().equals(to) && e.dest().equals(from)));

        invalidateCache();
    }

    /**
     * Invalidates all cached computation results.
     */
    public void invalidateCache() {
        sccCacheValid = false;
        bridgeInfoValid = false;
    }

    /**
     * Checks if an edge is a bridge (i.e., its removal increases components).
     * For undirected graphs, checks both edge directions.
     * Only valid for undirected graphs.
     *
     * @param u one end of the edge
     * @param v the other end of the edge
     * @return true if it is a bridge
     * @throws NullPointerException if u or v is null
     * @throws UnsupportedOperationException if graph is directed
     */
    public boolean isBridgeEdge(K u, K v) {
        Objects.requireNonNull(u, "First node cannot be null");
        Objects.requireNonNull(v, "Second node cannot be null");

        if (isDirected) {
            throw new UnsupportedOperationException("Bridge detection only supported for undirected graphs.");
        }

        computeTarjanIfNeeded();

        // Check both directions for undirected graphs
        for (DetailedEdge<K> bridge : cachedBridgeEdges) {
            if ((bridge.source().equals(u) && bridge.dest().equals(v)) ||
                    (bridge.source().equals(v) && bridge.dest().equals(u))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a node is an articulation point (bridge node).
     * Only valid for undirected graphs.
     *
     * @param node the node to check
     * @return true if it's a bridge node
     * @throws NullPointerException if node is null
     * @throws UnsupportedOperationException if graph is directed
     */
    public boolean isBridgeNode(K node) {
        Objects.requireNonNull(node, "Node cannot be null");

        if (isDirected) {
            throw new UnsupportedOperationException("Bridge detection only supported for undirected graphs.");
        }

        computeTarjanIfNeeded();
        return cachedArticulationPoints.contains(node);
    }

    /**
     * Returns all bridge edges in the graph.
     * Only valid for undirected graphs.
     *
     * @return set of bridge edges
     * @throws UnsupportedOperationException if graph is directed
     */
    public Set<DetailedEdge<K>> getBridgeEdges() {
        if (isDirected) {
            throw new UnsupportedOperationException("Bridge detection only supported for undirected graphs.");
        }

        computeTarjanIfNeeded();
        return Collections.unmodifiableSet(cachedBridgeEdges);
    }

    /**
     * Returns all articulation points in the graph.
     * Only valid for undirected graphs.
     *
     * @return set of bridge nodes
     * @throws UnsupportedOperationException if graph is directed
     */
    public Set<K> getBridgeNodes() {
        if (isDirected) {
            throw new UnsupportedOperationException("Bridge detection only supported for undirected graphs.");
        }

        computeTarjanIfNeeded();
        return Collections.unmodifiableSet(cachedArticulationPoints);
    }

    /**
     * Computes Tarjan's algorithm for bridge and articulation point detection.
     * Uses the corrected logic from the working unweighted graph implementation.
     */
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

        cachedArticulationPoints = new HashSet<>(articulationPoints);
        cachedBridgeEdges = new HashSet<>(bridgeEdges);
        bridgeInfoValid = true;
    }

    /**
     * Tarjan's DFS implementation with corrected time increment logic.
     * Adapted from the working unweighted graph implementation.
     */
    private void dfsTarjan(K u, K parent) {
        visitedTarjan.add(u);
        tin.put(u, time);
        low.put(u, time);
        time++; // Fixed: Separate time increment from assignment
        int children = 0;

        for (WeightedEdge<K> edge : adjacencyList.getOrDefault(u, List.of())) {
            K v = edge.node();
            if (v.equals(parent)) continue;

            if (!visitedTarjan.contains(v)) {
                children++;
                dfsTarjan(v, u);
                low.put(u, Math.min(low.get(u), low.get(v)));

                // Articulation point check
                if (parent != null && low.get(v) >= tin.get(u)) {
                    articulationPoints.add(u);
                }

                // Bridge edge check
                if (low.get(v) > tin.get(u)) {
                    bridgeEdges.add(new DetailedEdge<>(u, v, edge.weight()));
                }

            } else {
                // Back edge case
                low.put(u, Math.min(low.get(u), tin.get(v)));
            }
        }

        // Root articulation point check
        if (parent == null && children > 1) {
            articulationPoints.add(u);
        }
    }

    /**
     * Computes strongly connected components using Kosaraju's algorithm.
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public List<List<K>> getStronglyConnectedComponents() {
        if (!isDirected) {
            throw new UnsupportedOperationException("SCC is not defined for undirected graphs.");
        }
        if (sccCacheValid) return cachedSCCs;

        Set<K> visited = new HashSet<>();
        Deque<K> stack = new ArrayDeque<>();

        for (K node : adjacencyList.keySet()) {
            if (!visited.contains(node)) {
                dfsTopo(node, visited, stack);
            }
        }

        Map<K, List<K>> transpose = new HashMap<>();
        for (K u : adjacencyList.keySet()) {
            for (WeightedEdge<K> e : adjacencyList.get(u)) {
                transpose.computeIfAbsent(e.node(), k -> new ArrayList<>()).add(u);
            }
        }

        visited.clear();
        cachedSCCs = new ArrayList<>();
        cachedSccMap.clear();

        while (!stack.isEmpty()) {
            K node = stack.pop();
            if (!visited.contains(node)) {
                List<K> component = new ArrayList<>();
                dfsCollect(node, transpose, visited, component);
                for (K k : component) {
                    cachedSccMap.put(k, cachedSCCs.size());
                }
                cachedSCCs.add(component);
            }
        }

        sccCacheValid = true;
        return cachedSCCs;
    }

    /**
     * @return true if graph is strongly connected (only one SCC).
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public boolean isStronglyConnected() {
        return getSCCCount() == 1;
    }

    /**
     * @return number of strongly connected components.
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public int getSCCCount() {
        return getStronglyConnectedComponents().size();
    }

    /**
     * @return map from each node to its SCC ID.
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public Map<K, Integer> getSCCMap() {
        getStronglyConnectedComponents();
        return Collections.unmodifiableMap(cachedSccMap);
    }

    private void dfsTopo(K node, Set<K> visited, Deque<K> stack) {
        visited.add(node);
        for (WeightedEdge<K> e : adjacencyList.getOrDefault(node, List.of())) {
            if (!visited.contains(e.node())) {
                dfsTopo(e.node(), visited, stack);
            }
        }
        stack.push(node);
    }

    private void dfsCollect(K node, Map<K, List<K>> graph, Set<K> visited, List<K> component) {
        visited.add(node);
        component.add(node);
        for (K neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                dfsCollect(neighbor, graph, visited, component);
            }
        }
    }

    /**
     * Performs breadth-first traversal.
     *
     * @param start  the starting node
     * @param action action to perform on each visited node
     * @throws NullPointerException if start or action is null
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public void bfs(K start, Consumer<K> action) {
        Objects.requireNonNull(start, "Start node cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            K node = queue.poll();
            action.accept(node);
            for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(node, List.of())) {
                if (visited.add(neighbor.node())) {
                    queue.add(neighbor.node());
                }
            }
        }
    }

    /**
     * Performs depth-first traversal.
     *
     * @param start  the starting node
     * @param action action to perform on each visited node
     * @throws NullPointerException if start or action is null
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public void dfs(K start, Consumer<K> action) {
        Objects.requireNonNull(start, "Start node cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        Set<K> visited = new HashSet<>();
        dfsHelper(start, visited, action);
    }

    private void dfsHelper(K node, Set<K> visited, Consumer<K> action) {
        visited.add(node);
        action.accept(node);
        for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor.node())) {
                dfsHelper(neighbor.node(), visited, action);
            }
        }
    }

    /**
     * Dijkstra's algorithm for shortest path with non-negative weights.
     *
     * @param source      the starting node
     * @param destination the target node
     * @return shortest distance, or null if unreachable or nodes don't exist
     * @throws NullPointerException if source or destination is null
     *
     * <p><b>Time Complexity:</b> O((V + E) log V)</p>
     */
    @Override
    public Integer dijkstra(K source, K destination) {
        Objects.requireNonNull(source, "Source node cannot be null");
        Objects.requireNonNull(destination, "Destination node cannot be null");

        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return null;
        }

        Map<K, Integer> dist = new HashMap<>();
        for (K node : adjacencyList.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        PriorityQueue<WeightedEdge<K>> pq = new PriorityQueue<>();
        pq.offer(new WeightedEdge<>(source, 0));

        while (!pq.isEmpty()) {
            WeightedEdge<K> current = pq.poll();
            K u = current.node();

            if (current.weight() > dist.get(u)) continue;

            for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(u, List.of())) {
                if (neighbor.weight() < 0) {
                    throw new IllegalArgumentException("Graph contains negative edge weights.");
                }

                int newDist = dist.get(u) + neighbor.weight();
                if (newDist < dist.get(neighbor.node())) {
                    dist.put(neighbor.node(), newDist);
                    pq.offer(new WeightedEdge<>(neighbor.node(), newDist));
                }
            }
        }

        return dist.get(destination) == Integer.MAX_VALUE ? null : dist.get(destination);
    }

    /**
     * Bellman-Ford algorithm for shortest paths, allows negative weights.
     *
     * @param source the starting node
     * @return map of shortest distances from source to all nodes
     * @throws IllegalArgumentException  if source is invalid
     * @throws IllegalStateException     if negative weight cycle exists
     * @throws NullPointerException     if source is null
     *
     * <p><b>Time Complexity:</b> O(V * E)</p>
     */
    @Override
    public Map<K, Integer> bellmanFord(K source) {
        Objects.requireNonNull(source, "Source node cannot be null");

        if (!adjacencyList.containsKey(source)) {
            throw new IllegalArgumentException("Source node is invalid.");
        }

        Map<K, Integer> dist = new HashMap<>();
        for (K node : adjacencyList.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        int V = adjacencyList.size();
        for (int i = 0; i < V - 1; i++) {
            for (DetailedEdge<K> edge : allEdges) {
                K u = edge.source(), v = edge.dest();
                int w = edge.weight();
                if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                }
            }
        }

        for (DetailedEdge<K> edge : allEdges) {
            K u = edge.source(), v = edge.dest();
            int w = edge.weight();
            if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                throw new IllegalStateException("Graph contains a negative weight cycle.");
            }
        }

        return dist;
    }

    /**
     * Floyd-Warshall algorithm for all-pairs shortest paths.
     *
     * @return map of shortest distances between all pairs of nodes
     *
     * <p><b>Time Complexity:</b> O(V³)</p>
     */
    @Override
    public Map<K, Map<K, Integer>> floydWarshall() {
        Map<K, Map<K, Integer>> dist = new HashMap<>();

        for (K u : adjacencyList.keySet()) {
            dist.put(u, new HashMap<>());
            for (K v : adjacencyList.keySet()) {
                dist.get(u).put(v, u.equals(v) ? 0 : Integer.MAX_VALUE);
            }
        }

        for (K u : adjacencyList.keySet()) {
            for (WeightedEdge<K> edge : adjacencyList.get(u)) {
                dist.get(u).put(edge.node(), edge.weight());
            }
        }

        for (K k : adjacencyList.keySet()) {
            for (K i : adjacencyList.keySet()) {
                for (K j : adjacencyList.keySet()) {
                    int ik = dist.get(i).getOrDefault(k, Integer.MAX_VALUE);
                    int kj = dist.get(k).getOrDefault(j, Integer.MAX_VALUE);
                    int ij = dist.get(i).get(j);
                    if (ik != Integer.MAX_VALUE && kj != Integer.MAX_VALUE && ik + kj < ij) {
                        dist.get(i).put(j, ik + kj);
                    }
                }
            }
        }

        return dist;
    }

    /**
     * Prim's algorithm for Minimum Spanning Tree.
     *
     * @param start the starting node
     * @return total weight of the MST
     * @throws NullPointerException if start is null
     *
     * <p><b>Time Complexity:</b> O((V + E) log V)</p>
     */
    @Override
    public Integer primsMST(K start) {
        Objects.requireNonNull(start, "Start node cannot be null");

        Set<K> visited = new HashSet<>();
        PriorityQueue<WeightedEdge<K>> pq = new PriorityQueue<>();
        int totalCost = 0;

        pq.add(new WeightedEdge<>(start, 0));

        while (!pq.isEmpty()) {
            WeightedEdge<K> edge = pq.poll();
            K node = edge.node();
            if (!visited.add(node)) continue;
            totalCost += edge.weight();

            for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor.node())) {
                    pq.add(new WeightedEdge<>(neighbor.node(), neighbor.weight()));
                }
            }
        }

        return totalCost;
    }

    /**
     * Kruskal's algorithm to compute MST.
     *
     * @return total weight of the MST
     *
     * <p><b>Time Complexity:</b> O(E log E)</p>
     */
    @Override
    public Integer kruskalMST() {
        DisjointSet<K> dsu = new DisjointSet<>(adjacencyList.keySet());
        List<DetailedEdge<K>> sorted = new ArrayList<>(allEdges);
        sorted.sort(Comparator.comparingInt(DetailedEdge::weight));

        int totalCost = 0;
        for (DetailedEdge<K> edge : sorted) {
            if (dsu.union(edge.source(), edge.dest())) {
                totalCost += edge.weight();
            }
        }

        return totalCost;
    }

    /**
     * Checks if node 'to' is reachable from node 'from'.
     *
     * @param from start node
     * @param to   destination node
     * @return true if reachable
     * @throws NullPointerException if from or to is null
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    @Override
    public boolean isReachable(K from, K to) {
        Objects.requireNonNull(from, "Source node cannot be null");
        Objects.requireNonNull(to, "Destination node cannot be null");

        if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) return false;
        if (from.equals(to)) return true; // Optimization for same node

        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        visited.add(from);
        queue.add(from);

        while (!queue.isEmpty()) {
            K node = queue.poll();
            if (node.equals(to)) return true;
            for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(node, List.of())) {
                if (visited.add(neighbor.node())) {
                    queue.add(neighbor.node());
                }
            }
        }

        return false;
    }

    /**
     * Prints the adjacency list of the graph.
     */
    @Override
    public void printGraph() {
        for (var entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}