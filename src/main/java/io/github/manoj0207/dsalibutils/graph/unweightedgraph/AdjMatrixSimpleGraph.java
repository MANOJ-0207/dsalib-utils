package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic graph implementation using an adjacency matrix.
 * Supports both directed and undirected graphs.
 * <p>
 * Best suited for dense graphs or where constant-time edge existence checks are important.
 *
 * @param <K> the type of the node (vertex)
 */
public class AdjMatrixSimpleGraph<K> implements SimpleGraph<K>{

    // Maps each node to a unique index used in the matrix
    private final Map<K, Integer> nodeIndexMap = new HashMap<>();

    // Stores the reverse mapping from index to node
    private final List<K> indexNodeList = new ArrayList<>();

    // The adjacency matrix
    private boolean[][] matrix;

    // Whether the graph is directed
    private final boolean isDirected;

    // Bridge detection cache
    private boolean bridgeInfoValid = false;
    private final Set<Edge<K>> cachedBridgeEdges = new HashSet<>();
    private final Set<K> cachedBridgeNodes = new HashSet<>();

    /** Constructs an undirected graph by default. */
    public AdjMatrixSimpleGraph() {
        this(false);
    }

    /**
     * Constructs a graph with the specified directionality.
     *
     * @param isDirected {@code true} for a directed graph, {@code false} for undirected
     */
    public AdjMatrixSimpleGraph(boolean isDirected) {
        this.isDirected = isDirected;
        this.matrix = new boolean[10][10]; // initial matrix size
    }

    /**
     * Adds an edge between two nodes. If nodes do not exist, they are created.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException if either node is null
     */
    public void addEdge(K from, K to) {
        Objects.requireNonNull(from, "From-node cannot be null");
        Objects.requireNonNull(to, "To-node cannot be null");

        int u = getOrCreateIndex(from);
        int v = getOrCreateIndex(to);

        matrix[u][v] = true;
        if (!isDirected) matrix[v][u] = true;

        bridgeInfoValid = false; // invalidate bridge cache
    }

    /**
     * Removes an edge between two nodes if it exists.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException      if either node is null
     * @throws IllegalArgumentException  if either node does not exist in the graph
     */
    public void removeEdge(K from, K to) {
        Objects.requireNonNull(from, "From-node cannot be null");
        Objects.requireNonNull(to, "To-node cannot be null");

        Integer u = nodeIndexMap.get(from);
        Integer v = nodeIndexMap.get(to);

        // Gracefully return if either node is not present
        if (u == null || v == null) {
            return;
        }

        matrix[u][v] = false;
        if (!isDirected) matrix[v][u] = false;

        bridgeInfoValid = false; // Invalidate bridge cache
    }



    /**
     * Performs Breadth-First Search (BFS) starting from the given node.
     *
     * @param source the starting node
     * @param action a consumer to apply to each visited node
     */

    public void bfs(K source, Consumer<K> action) {
        if (!nodeIndexMap.containsKey(source)) return;

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        int srcIdx = nodeIndexMap.get(source);
        visited.add(srcIdx);
        queue.offer(srcIdx);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            action.accept(indexNodeList.get(curr));
            for (int i = 0; i < indexNodeList.size(); i++) {
                if (matrix[curr][i] && visited.add(i)) {
                    queue.offer(i);
                }
            }
        }
    }

    /**
     * Performs Depth-First Search (DFS) starting from the given node.
     *
     * @param source the starting node
     * @param action a consumer to apply to each visited node
     */
    public void dfs(K source, Consumer<K> action) {
        if (!nodeIndexMap.containsKey(source)) return;
        Set<Integer> visited = new HashSet<>();
        dfsHelper(nodeIndexMap.get(source), visited, action);
    }

    /**
     * Returns a topological ordering of the graph.
     * Valid only for directed acyclic graphs (DAGs).
     *
     * @return list of nodes in topological order
     * @throws UnsupportedOperationException if the graph is undirected
     */
    public List<K> getTopologicalSort() {
        if (!isDirected) {
            throw new UnsupportedOperationException("Topological sort only supported for directed graphs.");
        }

        Set<Integer> visited = new HashSet<>();
        Deque<K> stack = new ArrayDeque<>();

        for (int i = 0; i < indexNodeList.size(); i++) {
            if (!visited.contains(i)) {
                topoDfs(i, visited, stack);
            }
        }

        return new ArrayList<>(stack);
    }

    /**
     * Returns shortest distances from a given source node to all reachable nodes using BFS.
     *
     * @param source the starting node
     * @return map of nodes to their shortest distance from the source
     */
    public Map<K, Integer> shortestDistances(K source) {
        Map<K, Integer> dist = new HashMap<>();
        if (!nodeIndexMap.containsKey(source)) return dist;

        int srcIdx = nodeIndexMap.get(source);
        Queue<Integer> queue = new LinkedList<>();
        dist.put(indexNodeList.get(srcIdx), 0);
        queue.offer(srcIdx);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            int currDist = dist.get(indexNodeList.get(curr));

            for (int i = 0; i < indexNodeList.size(); i++) {
                if (matrix[curr][i]) {
                    K neighbor = indexNodeList.get(i);
                    if (!dist.containsKey(neighbor)) {
                        dist.put(neighbor, currDist + 1);
                        queue.offer(i);
                    }
                }
            }
        }

        return dist;
    }

    /**
     * Checks whether there is a path from node {@code from} to node {@code to}.
     *
     * @param from source node
     * @param to   target node
     * @return {@code true} if reachable, else {@code false}
     */
    public boolean isReachable(K from, K to) {
        if (!nodeIndexMap.containsKey(from) || !nodeIndexMap.containsKey(to)) return false;

        int fromIdx = nodeIndexMap.get(from);
        int toIdx = nodeIndexMap.get(to);

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(fromIdx);
        queue.offer(fromIdx);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            if (curr == toIdx) return true;

            for (int i = 0; i < indexNodeList.size(); i++) {
                if (matrix[curr][i] && visited.add(i)) {
                    queue.offer(i);
                }
            }
        }

        return false;
    }

    /**
     * Checks if the graph is fully connected (i.e., all nodes are reachable from any node).
     * In directed graphs, this checks reachability from the first added node.
     *
     * @return {@code true} if the graph is connected, {@code false} otherwise
     */
    public boolean isConnected() {
        if (indexNodeList.isEmpty()) return true;

        Set<Integer> visited = new HashSet<>();
        dfsHelper(0, visited, k -> {});
        return visited.size() == indexNodeList.size();
    }

    /**
     * Checks if the given edge is a bridge.
     * A bridge is an edge whose removal increases the number of connected components.
     *
     * @param u one end of the edge
     * @param v the other end of the edge
     * @return {@code true} if the edge is a bridge, {@code false} otherwise
     */
    public boolean isBridgeEdge(K u, K v) {
        computeBridgesIfNeeded();
        return cachedBridgeEdges.contains(new Edge<>(u, v)) ||
                (!isDirected && cachedBridgeEdges.contains(new Edge<>(v, u)));
    }

    /**
     * Returns the set of all bridge edges in the graph.
     *
     * A bridge edge (also called a cut-edge) is an edge whose removal increases the number of connected components
     * in the graph. For undirected graphs, this means the edge is critical for connectivity. In directed graphs,
     * this returns edges based on Tarjan's algorithm, though interpretation may vary based on use case.
     *
     * This method uses a cached result that is recomputed only when the structure of the graph changes
     * (such as when edges are added or removed).
     *
     * @return an unmodifiable set of bridge edges in the graph. Each bridge is represented as an {@code Edge<K>}.
     *         For undirected graphs, only one direction of each edge is included.
     */
    @Override
    public Set<Edge<K>> getBridgeEdges() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeEdges);
    }

    /**
     * Returns the set of all bridge nodes (articulation points).
     *
     * @return an unmodifiable set of all bridge nodes in the graph
     */
    public Set<K> getBridgeNodes() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeNodes);
    }



    /**
     * Checks if a node is a bridge node (articulation point).
     * A bridge node is a node whose removal increases the number of connected components.
     *
     * @param node the node to check
     * @return {@code true} if the node is a bridge node, {@code false} otherwise
     */
    public boolean isBridgeNode(K node) {
        computeBridgesIfNeeded();
        return cachedBridgeNodes.contains(node);
    }

    // ---- Internal Utilities ----

    /** Ensures matrix can accommodate up to the given size. */
    private void ensureCapacity(int size) {
        if (size <= matrix.length) return;

        int newSize = Math.max(matrix.length * 2, size);
        boolean[][] newMatrix = new boolean[newSize][newSize];

        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix[i].length);
        }

        matrix = newMatrix;
    }

    /** Returns index of node, creates if not already indexed. */
    private int getOrCreateIndex(K node) {
        return nodeIndexMap.computeIfAbsent(node, k -> {
            int idx = indexNodeList.size();
            indexNodeList.add(k);
            ensureCapacity(idx + 1);
            return idx;
        });
    }

    /** DFS traversal helper. */
    private void dfsHelper(int idx, Set<Integer> visited, Consumer<K> action) {
        visited.add(idx);
        action.accept(indexNodeList.get(idx));
        for (int i = 0; i < indexNodeList.size(); i++) {
            if (matrix[idx][i] && !visited.contains(i)) {
                dfsHelper(i, visited, action);
            }
        }
    }

    /** DFS for topological sorting. */
    private void topoDfs(int idx, Set<Integer> visited, Deque<K> stack) {
        visited.add(idx);
        for (int i = 0; i < indexNodeList.size(); i++) {
            if (matrix[idx][i] && !visited.contains(i)) {
                topoDfs(i, visited, stack);
            }
        }
        stack.push(indexNodeList.get(idx));
    }

    /** Computes bridges and articulation points (bridge nodes) using Tarjan's Algorithm. */
    private void computeBridgesIfNeeded() {
        if (bridgeInfoValid) return;

        int n = indexNodeList.size();
        int[] tin = new int[n];       // discovery time
        int[] low = new int[n];       // lowest reachable discovery time
        boolean[] visited = new boolean[n];
        int[] time = {0};

        cachedBridgeEdges.clear();
        cachedBridgeNodes.clear();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsBridgeAndArticulation(i, -1, visited, tin, low, time, new int[]{0});
            }
        }

        bridgeInfoValid = true;
    }

    /** DFS helper for finding bridges and articulation points. */
    private void dfsBridgeAndArticulation(int u, int parent, boolean[] visited,
                                          int[] tin, int[] low, int[] time, int[] childCount) {
        visited[u] = true;
        tin[u] = low[u] = ++time[0];
        int children = 0;

        for (int v = 0; v < indexNodeList.size(); v++) {
            if (!matrix[u][v]) continue;
            if (v == parent) continue;

            if (!visited[v]) {
                dfsBridgeAndArticulation(v, u, visited, tin, low, time, childCount);
                low[u] = Math.min(low[u], low[v]);

                // Check for bridge
                if (low[v] > tin[u]) {
                    K ku = indexNodeList.get(u);
                    K kv = indexNodeList.get(v);
                    cachedBridgeEdges.add(new Edge<>(ku, kv));
                }

                // Check for articulation point (excluding root case)
                if (parent != -1 && low[v] >= tin[u]) {
                    cachedBridgeNodes.add(indexNodeList.get(u));
                }

                children++;
            } else {
                low[u] = Math.min(low[u], tin[v]);
            }
        }

        // Special case for root node
        if (parent == -1 && children > 1) {
            cachedBridgeNodes.add(indexNodeList.get(u));
        }
    }
}
