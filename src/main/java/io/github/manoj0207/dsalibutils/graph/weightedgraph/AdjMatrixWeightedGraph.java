package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import io.github.manoj0207.dsalibutils.graph.disjointset.DisjointSet;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.DetailedEdge;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.WeightedEdge;
import io.github.manoj0207.dsalibutils.graph.unweightedgraph.Edge;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a weighted graph using an adjacency matrix.
 * Supports both directed and undirected graphs.
 *
 * @param <K> the type of nodes (vertices) in the graph
 */
public class AdjMatrixWeightedGraph<K> implements WeightedGraph<K> {

    private final Map<K, Integer> nodeIndexMap = new HashMap<>();
    private final List<K> indexNodeList = new ArrayList<>();
    private final List<DetailedEdge<K>> allEdges = new ArrayList<>();
    private int[][] matrix;
    private final boolean isDirected;
    private int size = 0;

    // Bridge computation caching
    private boolean bridgeInfoValid = false;
    private final Set<DetailedEdge<K>> cachedBridgeEdges = new HashSet<>();
    private final Set<K> cachedBridgeNodes = new HashSet<>();

    /**
     * Constructs an empty weighted graph.
     *
     * @param isDirected true for directed graph, false for undirected
     */
    public AdjMatrixWeightedGraph(boolean isDirected) {
        this.isDirected = isDirected;
        this.matrix = new int[10][10];
        for (int[] row : matrix) Arrays.fill(row, Integer.MAX_VALUE);
    }

    /** Ensures the internal matrix capacity is sufficient. */
    private void ensureCapacity() {
        if (size == matrix.length) {
            int newSize = size * 2;
            int[][] newMatrix = new int[newSize][newSize];
            for (int[] row : newMatrix) Arrays.fill(row, Integer.MAX_VALUE);
            for (int i = 0; i < size; i++) {
                System.arraycopy(matrix[i], 0, newMatrix[i], 0, size);
            }
            matrix = newMatrix;
        }
    }

    /** Registers a node if not already present. */
    private void registerNode(K node) {
        if (!nodeIndexMap.containsKey(node)) {
            ensureCapacity();
            nodeIndexMap.put(node, size);
            indexNodeList.add(node);
            matrix[size][size] = 0;
            size++;
        }
    }

    /**
     * Adds an edge from {@code from} to {@code to} with the specified {@code weight}.
     *
     * <p><b>Time Complexity:</b> O(1) average</p>
     *
     * @param from the source vertex
     * @param to the destination vertex
     * @param weight the weight of the edge
     */
    @Override
    public void addEdge(K from, K to, int weight) {
        registerNode(from);
        registerNode(to);
        int u = nodeIndexMap.get(from);
        int v = nodeIndexMap.get(to);
        matrix[u][v] = weight;
        if (!isDirected) {
            matrix[v][u] = weight;
        }
        allEdges.add(new DetailedEdge<>(from, to, weight));

        // Invalidate bridge cache since graph structure changed
        bridgeInfoValid = false;
    }

    /**
     * Removes the edge between {@code from} and {@code to}.
     *
     * <p><b>Time Complexity:</b> O(E) where E is the number of edges (due to list removal)</p>
     *
     * @param from the source vertex
     * @param to the destination vertex
     */
    @Override
    public void removeEdge(K from, K to) {
        if (!nodeIndexMap.containsKey(from) || !nodeIndexMap.containsKey(to)) return;
        int u = nodeIndexMap.get(from);
        int v = nodeIndexMap.get(to);
        matrix[u][v] = Integer.MAX_VALUE;
        if (!isDirected) {
            matrix[v][u] = Integer.MAX_VALUE;
        }
        allEdges.removeIf(e -> (e.source().equals(from) && e.dest().equals(to)) ||
                (!isDirected && e.source().equals(to) && e.dest().equals(from)));

        // Invalidate bridge cache since graph structure changed
        bridgeInfoValid = false;
    }

    /**
     * Performs BFS traversal from the starting node and prints nodes.
     *
     * <p><b>Time Complexity:</b> O(V²) in dense matrix</p>
     *
     * @param start the starting vertex
     */
    public void bfs(K start) {
        bfs(start, k -> System.out.print(k + " "));
        System.out.println();
    }

    /**
     * Performs BFS traversal from the {@code start} vertex and applies {@code action}
     * to each visited node.
     *
     * <p><b>Time Complexity:</b> O(V²) in dense matrix</p>
     *
     * @param start the starting vertex
     * @param action a Consumer to process each visited vertex
     */
    public void bfs(K start, Consumer<K> action) {
        if (!nodeIndexMap.containsKey(start)) return;
        boolean[] visited = new boolean[size];
        Queue<K> queue = new LinkedList<>();
        queue.add(start);
        visited[nodeIndexMap.get(start)] = true;
        while (!queue.isEmpty()) {
            K node = queue.poll();
            action.accept(node);
            int u = nodeIndexMap.get(node);
            for (int v = 0; v < size; v++) {
                if (matrix[u][v] != Integer.MAX_VALUE && !visited[v]) {
                    visited[v] = true;
                    queue.add(indexNodeList.get(v));
                }
            }
        }
    }

    /**
     * Performs DFS traversal from the starting node and prints nodes.
     *
     * <p><b>Time Complexity:</b> O(V²) in dense matrix</p>
     *
     * @param start the starting vertex
     */
    public void dfs(K start) {
        dfs(start, k -> System.out.print(k + " "));
        System.out.println();
    }

    /**
     * Performs DFS traversal from {@code start} and applies {@code action}
     * on each visited node.
     *
     * <p><b>Time Complexity:</b> O(V²) in dense matrix</p>
     *
     * @param start the starting vertex
     * @param action the action to apply to each visited vertex
     */
    public void dfs(K start, Consumer<K> action) {
        if (!nodeIndexMap.containsKey(start)) return;
        boolean[] visited = new boolean[size];
        dfsHelper(start, visited, action);
    }

    private void dfsHelper(K node, boolean[] visited, Consumer<K> action) {
        int u = nodeIndexMap.get(node);
        visited[u] = true;
        action.accept(node);
        for (int v = 0; v < size; v++) {
            if (matrix[u][v] != Integer.MAX_VALUE && !visited[v]) {
                dfsHelper(indexNodeList.get(v), visited, action);
            }
        }
    }

    /**
     * Determines if {@code to} is reachable from {@code from}.
     *
     * <p><b>Time Complexity:</b> O(V²)</p>
     *
     * @param from source vertex
     * @param to destination vertex
     * @return {@code true} if reachable, {@code false} otherwise
     */
    @Override
    public boolean isReachable(K from, K to) {
        if (!nodeIndexMap.containsKey(from) || !nodeIndexMap.containsKey(to)) return false;
        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        queue.add(from);
        visited.add(from);
        while (!queue.isEmpty()) {
            K node = queue.poll();
            if (node.equals(to)) return true;
            int u = nodeIndexMap.get(node);
            for (int v = 0; v < size; v++) {
                if (matrix[u][v] != Integer.MAX_VALUE && visited.add(indexNodeList.get(v))) {
                    queue.add(indexNodeList.get(v));
                }
            }
        }
        return false;
    }

    /**
     * Computes the shortest path distance from {@code source} to {@code destination}
     * using Dijkstra's algorithm (non-negative weights).
     *
     * <p><b>Time Complexity:</b> O(V² log V)</p>
     *
     * @param source the starting vertex
     * @param destination the target vertex
     * @return shortest distance or {@code null} if unreachable
     */
    @Override
    public Integer dijkstra(K source, K destination) {
        if (!nodeIndexMap.containsKey(source) || !nodeIndexMap.containsKey(destination)) return null;
        int[] dist = new int[size];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[nodeIndexMap.get(source)] = 0;
        PriorityQueue<WeightedEdge<Integer>> pq = new PriorityQueue<>();
        pq.add(new WeightedEdge<>(nodeIndexMap.get(source), 0));
        boolean[] visited = new boolean[size];
        while (!pq.isEmpty()) {
            WeightedEdge<Integer> edge = pq.poll();
            int u = edge.node();
            if (visited[u]) continue;
            visited[u] = true;
            for (int v = 0; v < size; v++) {
                if (matrix[u][v] != Integer.MAX_VALUE) {
                    int newDist = dist[u] + matrix[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        pq.add(new WeightedEdge<>(v, newDist));
                    }
                }
            }
        }
        int d = dist[nodeIndexMap.get(destination)];
        return d == Integer.MAX_VALUE ? null : d;
    }

    /**
     * Computes shortest paths from {@code source} to all vertices
     * using the Bellman–Ford algorithm (allows negative weights).
     *
     * <p><b>Time Complexity:</b> O(V·E)</p>
     *
     * @param source the starting vertex
     * @return map of distances from source to each vertex
     * @throws IllegalStateException if a negative-weight cycle is detected
     */
    @Override
    public Map<K, Integer> bellmanFord(K source) {
        Map<K, Integer> dist = new HashMap<>();
        for (K node : nodeIndexMap.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);
        for (int i = 0; i < size - 1; i++) {
            for (DetailedEdge<K> edge : allEdges) {
                K u = edge.source();
                K v = edge.dest();
                int w = edge.weight();
                if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                }
            }
        }
        for (DetailedEdge<K> edge : allEdges) {
            K u = edge.source();
            K v = edge.dest();
            int w = edge.weight();
            if (dist.get(u) != Integer.MAX_VALUE && dist.get(u) + w < dist.get(v)) {
                throw new IllegalStateException("Negative weight cycle detected");
            }
        }
        return dist;
    }

    /**
     * Computes MST total weight using Prim's algorithm (valid for both directed and undirected).
     *
     * <p><b>Time Complexity:</b> O(V² log V)</p>
     *
     * @param start the starting vertex
     * @return total weight of MST, or null if start not in graph
     */
    @Override
    public Integer primsMST(K start) {
        if (!nodeIndexMap.containsKey(start)) return null;
        boolean[] visited = new boolean[size];
        PriorityQueue<WeightedEdge<Integer>> pq = new PriorityQueue<>();
        pq.add(new WeightedEdge<>(nodeIndexMap.get(start), 0));
        int totalCost = 0;
        while (!pq.isEmpty()) {
            WeightedEdge<Integer> edge = pq.poll();
            int u = edge.node();
            if (visited[u]) continue;
            visited[u] = true;
            totalCost += edge.weight();
            for (int v = 0; v < size; v++) {
                if (matrix[u][v] != Integer.MAX_VALUE && !visited[v]) {
                    pq.add(new WeightedEdge<>(v, matrix[u][v]));
                }
            }
        }
        return totalCost;
    }

    /**
     * Computes MST total weight with Kruskal's algorithm (undirected graph only).
     *
     * <p><b>Time Complexity:</b> O(E log E)</p>
     *
     * @return total weight of MST
     */
    @Override
    public Integer kruskalMST() {
        DisjointSet<K> dsu = new DisjointSet<>(nodeIndexMap.keySet());
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
     * Computes all-pairs shortest paths using Floyd–Warshall algorithm.
     *
     * <p><b>Time Complexity:</b> O(V³)</p>
     *
     * @return a map of source → (target → distance)
     */
    public Map<K, Map<K, Integer>> floydWarshall() {
        Map<K, Map<K, Integer>> dist = new HashMap<>();
        for (K i : nodeIndexMap.keySet()) {
            dist.put(i, new HashMap<>());
            for (K j : nodeIndexMap.keySet()) {
                dist.get(i).put(j, i.equals(j) ? 0 : Integer.MAX_VALUE);
            }
        }
        for (DetailedEdge<K> edge : allEdges) {
            dist.get(edge.source()).put(edge.dest(), edge.weight());
            if (!isDirected) {
                dist.get(edge.dest()).put(edge.source(), edge.weight());
            }
        }
        for (K k : nodeIndexMap.keySet()) {
            for (K i : nodeIndexMap.keySet()) {
                for (K j : nodeIndexMap.keySet()) {
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
     * Computes strongly connected components using Kosaraju's algorithm
     * (only valid for directed graphs).
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     *
     * @return list of SCCs, each as a list of vertices
     * @throws UnsupportedOperationException if graph is undirected
     */
    @Override
    public List<List<K>> getStronglyConnectedComponents() {
        if (!isDirected) {
            throw new UnsupportedOperationException("SCCs are only defined for directed graphs.");
        }
        boolean[] visited = new boolean[size];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                fillOrder(i, visited, stack);
            }
        }
        boolean[][] transposed = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] != Integer.MAX_VALUE) {
                    transposed[j][i] = true;
                }
            }
        }
        Arrays.fill(visited, false);
        List<List<K>> sccList = new ArrayList<>();
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!visited[node]) {
                List<K> component = new ArrayList<>();
                dfsTranspose(node, visited, transposed, component);
                sccList.add(component);
            }
        }
        return sccList;
    }

    private void fillOrder(int node, boolean[] visited, Deque<Integer> stack) {
        visited[node] = true;
        for (int i = 0; i < size; i++) {
            if (matrix[node][i] != Integer.MAX_VALUE && !visited[i]) {
                fillOrder(i, visited, stack);
            }
        }
        stack.push(node);
    }

    private void dfsTranspose(int node, boolean[] visited, boolean[][] transposed, List<K> component) {
        visited[node] = true;
        component.add(indexNodeList.get(node));
        for (int i = 0; i < size; i++) {
            if (transposed[node][i] && !visited[i]) {
                dfsTranspose(i, visited, transposed, component);
            }
        }
    }

    /**
     * Checks if the graph is strongly connected.
     *
     * <p><b>Time Complexity:</b> O(V·(V+E)) worst-case</p>
     *
     * @return {@code true} if strongly connected
     */
    @Override
    public boolean isStronglyConnected() {
        if (!isDirected) {
            if (size == 0) return true;
            boolean[] visited = new boolean[size];
            dfsHelper(indexNodeList.get(0), visited, k -> {});
            for (boolean b : visited) {
                if (!b) return false;
            }
            return true;
        } else {
            for (K from : indexNodeList) {
                Set<K> reachable = new HashSet<>();
                boolean[] visited = new boolean[size];
                dfsHelper(from, visited, reachable::add);
                if (reachable.size() != size) return false;
            }
            return true;
        }
    }

    /**
     * Returns the number of strongly connected components (SCCs).
     *
     * <p><b>Time Complexity:</b> same as getStronglyConnectedComponents()</p>
     *
     * @return count of SCCs
     */
    @Override
    public int getSCCCount() {
        return getStronglyConnectedComponents().size();
    }

    /**
     * Returns a map of each vertex to its SCC component index.
     *
     * <p><b>Time Complexity:</b> same as getStronglyConnectedComponents()</p>
     *
     * @return map vertex → SCC index
     */
    @Override
    public Map<K, Integer> getSCCMap() {
        List<List<K>> sccs = getStronglyConnectedComponents();
        Map<K, Integer> map = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (K node : sccs.get(i)) {
                map.put(node, i);
            }
        }
        return map;
    }

    /**
     * Checks if an edge is a bridge (i.e. its removal disconnects the graph).
     * For weighted graphs, this considers only the existence of edges, not their weights.
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return {@code true} if it is a bridge
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    public boolean isBridgeEdge(K u, K v) {
        computeBridgesIfNeeded();
        int weight = getWeight(u, v);
        if (weight == Integer.MAX_VALUE) return false;
        return cachedBridgeEdges.contains(new DetailedEdge<>(u, v, weight)) ||
                (!isDirected && cachedBridgeEdges.contains(new DetailedEdge<>(v, u, weight)));

    }

    private int getWeight(K u, K v) {
        Integer ui = nodeIndexMap.get(u);
        Integer vi = nodeIndexMap.get(v);
        if (ui == null || vi == null) return Integer.MAX_VALUE;
        return matrix[ui][vi];
    }


    /**
     * Returns all bridge edges in the graph.
     * For weighted graphs, this considers only the existence of edges, not their weights.
     *
     * @return an unmodifiable set of bridge edges
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    public Set<DetailedEdge<K>> getBridgeEdges() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeEdges);
    }

    /**
     * Returns all bridge nodes (articulation points).
     * For weighted graphs, this considers only the graph structure, not edge weights.
     *
     * @return an unmodifiable set of bridge nodes
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    public Set<K> getBridgeNodes() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeNodes);
    }

    /**
     * Checks if the node is a bridge node (articulation point).
     * For weighted graphs, this considers only the graph structure, not edge weights.
     *
     * @param node the node to check
     * @return {@code true} if it is a bridge node
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    public boolean isBridgeNode(K node) {
        computeBridgesIfNeeded();
        return cachedBridgeNodes.contains(node);
    }

    // -- Bridge computation utilities --

    /**
     * Computes bridge edges and nodes if not already computed or if cache is invalid.
     * Uses Tarjan's bridge-finding algorithm adapted for weighted graphs.
     *
     * <p><b>Time Complexity:</b> O(V + E)</p>
     */
    private void computeBridgesIfNeeded() {
        if (bridgeInfoValid) return;

        int[] tin = new int[size], low = new int[size];
        boolean[] visited = new boolean[size];
        int[] time = {0};

        cachedBridgeEdges.clear();
        cachedBridgeNodes.clear();

        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                dfsBridgeAndArticulation(i, -1, visited, tin, low, time);
            }
        }
        bridgeInfoValid = true;
    }

    /**
     * DFS helper for finding bridges and articulation points in weighted graphs.
     * Adapted from Tarjan's algorithm to work with the weighted adjacency matrix.
     */
    private void dfsBridgeAndArticulation(int u, int parent, boolean[] visited,
                                          int[] tin, int[] low, int[] time) {
        visited[u] = true;
        tin[u] = low[u] = ++time[0];
        int children = 0;

        for (int v = 0; v < size; v++) {
            // Skip if no edge exists or if it's the parent edge
            if (matrix[u][v] == Integer.MAX_VALUE || v == parent) continue;

            if (!visited[v]) {
                dfsBridgeAndArticulation(v, u, visited, tin, low, time);
                low[u] = Math.min(low[u], low[v]);

                // Bridge edge condition
                if (low[v] > tin[u]) {
                    cachedBridgeEdges.add(new DetailedEdge<>(indexNodeList.get(u),
                            indexNodeList.get(v), matrix[u][v]));
                }

                // Articulation point condition (non-root)
                if (parent != -1 && low[v] >= tin[u]) {
                    cachedBridgeNodes.add(indexNodeList.get(u));
                }
                children++;
            } else {
                // Back edge
                low[u] = Math.min(low[u], tin[v]);
            }
        }

        // Articulation point condition (root with multiple children)
        if (parent == -1 && children > 1) {
            cachedBridgeNodes.add(indexNodeList.get(u));
        }
    }

    /**
     * Prints the adjacency matrix with vertex labels (for debugging).
     *
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public void printGraph() {
        System.out.print("  ");
        for (K node : indexNodeList) {
            System.out.print(node + " ");
        }
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(indexNodeList.get(i) + " ");
            for (int j = 0; j < size; j++) {
                System.out.print((matrix[i][j] == Integer.MAX_VALUE ? "∞" : matrix[i][j]) + " ");
            }
            System.out.println();
        }
    }
}