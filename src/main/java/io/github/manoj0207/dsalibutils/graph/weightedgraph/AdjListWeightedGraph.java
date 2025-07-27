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
 * @param <K> the type of the nodes (vertices)
 */
public class AdjListWeightedGraph<K> implements WeightedGraph<K>{
    private final Map<K, List<WeightedEdge<K>>> adjacencyList = new HashMap<>();
    private final List<DetailedEdge<K>> allEdges = new ArrayList<>();
    private final boolean isDirected;

    // SCC (Kosaraju) cache
    private boolean sccCacheValid = false;
    private List<List<K>> cachedSCCs = new ArrayList<>();
    private Map<K, Integer> cachedSccMap = new HashMap<>();

    public AdjListWeightedGraph(boolean isDirected) {
        this.isDirected = isDirected;
    }

    /**
     * Adds an edge to the graph. For undirected graphs, adds both directions.
     * Invalidates cached SCC.
     */
    @Override
    public void addEdge(K from, K to, int weight) {
        // Ensure both nodes are in the map even if no outgoing edge
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>());
        adjacencyList.computeIfAbsent(to, k -> new ArrayList<>()); // ← fix here

        adjacencyList.get(from).add(new WeightedEdge<>(to, weight));
        if (!isDirected) {
            adjacencyList.get(to).add(new WeightedEdge<>(from, weight));
        }

        allEdges.add(new DetailedEdge<>(from, to, weight));
        invalidateCache();
    }


    /**
     * Removes an edge from the graph.
     * Invalidates cached SCC.
     */
    @Override
    public void removeEdge(K from, K to) {
        adjacencyList.getOrDefault(from, new ArrayList<>()).removeIf(e -> e.node().equals(to));
        if (!isDirected) {
            adjacencyList.getOrDefault(to, new ArrayList<>()).removeIf(e -> e.node().equals(from));
        }
        allEdges.removeIf(e -> (e.source().equals(from) && e.dest().equals(to)) ||
                (!isDirected && e.source().equals(to) && e.dest().equals(from)));
        invalidateCache();
    }

    /**
     * Invalidates cached SCCs.
     */
    public void invalidateCache() {
        sccCacheValid = false;
    }

    /**
     * @return List of strongly connected components in the graph.
     */
    @Override
    public List<List<K>> getStronglyConnectedComponents()
    {
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

        // Transpose graph
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
     * @return true if graph is strongly connected (i.e. only 1 SCC).
     */
    @Override
    public boolean isStronglyConnected() {
        return getSCCCount() == 1;
    }

    /**
     * @return number of strongly connected components.
     */
    @Override
    public int getSCCCount() {
        return getStronglyConnectedComponents().size();
    }

    /**
     * @return a map from each node to its SCC ID.
     */
    @Override
    public Map<K, Integer> getSCCMap() {
        getStronglyConnectedComponents(); // ensures cache is built
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
     * Performs BFS with an action on each visited node.
     */
    @Override
    public void bfs(K start, Consumer<K> action) {
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
     * Performs DFS with an action on each visited node.
     */
    @Override
    public void dfs(K start, Consumer<K> action) {
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
     * Computes the shortest path from source to destination using Dijkstra's algorithm.
     * Only valid for graphs with non-negative edge weights.
     *
     * @param source      starting node
     * @param destination destination node
     * @return shortest path cost or -1 if unreachable or nodes are invalid
     */
    @Override
    public int dijkstra(K source, K destination) {
        if (source == null || destination == null || !adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return -1;
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

            if (current.weight() > dist.get(u)) continue; // Skip outdated

            for (WeightedEdge<K> neighbor : adjacencyList.getOrDefault(u, List.of())) {
                if (neighbor.weight() < 0) {
                    throw new IllegalArgumentException("Graph contains negative edge weights. Dijkstra's algorithm cannot handle them.");
                }

                int newDist = dist.get(u) + neighbor.weight();
                if (newDist < dist.get(neighbor.node())) {
                    dist.put(neighbor.node(), newDist);
                    pq.offer(new WeightedEdge<>(neighbor.node(), newDist));
                }
            }
        }

        return dist.get(destination) == Integer.MAX_VALUE ? -1 : dist.get(destination);
    }

    /**
     * Computes shortest distances from the source node to all other nodes using the Bellman-Ford algorithm.
     * Can handle negative weights and detects negative weight cycles.
     *
     * @param source the starting node
     * @return a map of shortest distances from source to all nodes
     * @throws IllegalArgumentException if source is null or not in the graph
     * @throws IllegalStateException if the graph contains a negative weight cycle
     */

    @Override
    public Map<K, Integer> bellmanFord(K source) {
        if (source == null || !adjacencyList.containsKey(source)) {
            throw new IllegalArgumentException("Source node is invalid or not present in the graph.");
        }

        Map<K, Integer> dist = new HashMap<>();
        for (K node : adjacencyList.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        int V = adjacencyList.size();

        // Relax all edges V - 1 times
        for (int i = 0; i < V - 1; i++) {
            for (DetailedEdge<K> edge : allEdges) {
                K u = edge.source();
                K v = edge.dest();
                int w = edge.weight();

                if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                }
            }
        }

        // Check for negative weight cycles
        for (DetailedEdge<K> edge : allEdges) {
            K u = edge.source();
            K v = edge.dest();
            int w = edge.weight();

            if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                throw new IllegalStateException("Graph contains a negative weight cycle.");
            }
        }

        return dist;
    }

    /**
     * Computes the shortest distances between all pairs of nodes using the Floyd-Warshall algorithm.
     * This algorithm works for graphs with positive or negative edge weights (but no negative cycles).
     * It uses dynamic programming to find the minimum distance between every pair of nodes.
     *
     * @return a map where each key is a source node, and its value is another map representing
     *         the shortest distance to every destination node. If a destination is unreachable,
     *         the corresponding distance will be {@link Integer#MAX_VALUE}.
     */
    @Override
    public Map<K, Map<K, Integer>> floydWarshall() {
        Map<K, Map<K, Integer>> dist = new HashMap<>();

        for (K u : adjacencyList.keySet()) {
            dist.put(u, new HashMap<>());
            for (K v : adjacencyList.keySet()) {
                if (u.equals(v)) {
                    dist.get(u).put(v, 0);
                } else {
                    dist.get(u).put(v, Integer.MAX_VALUE);
                }
            }
        }

        // Step 2: Edge weights
        for (K u : adjacencyList.keySet()) {
            for (WeightedEdge<K> edge : adjacencyList.get(u)) {
                dist.get(u).put(edge.node(), edge.weight());
            }
        }

        // Step 3: Floyd-Warshall core
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
     * Prim's algorithm to find Minimum Spanning Tree.
     */
    @Override
    public int primsMST(K start) {
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
     * Kruskal's algorithm to find MST (only valid for undirected graphs).
     */
    @Override
    public int kruskalMST() {
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
     * @return true if node `to` is reachable from node `from`.
     */
    @Override
    public boolean isReachable(K from, K to) {
        if (!adjacencyList.containsKey(from)) return false;
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
     * Prints the adjacency list.
     */
    @Override
    public void printGraph() {
        for (var entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
