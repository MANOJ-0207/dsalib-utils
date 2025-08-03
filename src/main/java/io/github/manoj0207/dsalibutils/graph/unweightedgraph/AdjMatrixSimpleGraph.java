package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic graph implementation using an adjacency matrix.
 * <p>
 * Supports both directed and undirected graphs depending on the constructor argument.
 * <br>
 * Uses a dynamic 2D matrix for edge representation. Suitable for dense graphs or graphs
 * requiring constant-time edge existence checks.
 *
 * @param <K> the type of the node (vertex)
 */
public class AdjMatrixSimpleGraph<K> implements SimpleGraph<K> {

    private final Map<K, Integer> nodeIndexMap = new HashMap<>();
    private final List<K> indexNodeList = new ArrayList<>();
    private boolean[][] matrix;
    private final boolean isDirected;

    private boolean bridgeInfoValid = false;
    private final Set<Edge<K>> cachedBridgeEdges = new HashSet<>();
    private final Set<K> cachedBridgeNodes = new HashSet<>();

    /**
     * Constructs an undirected graph by default.
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public AdjMatrixSimpleGraph() {
        this(false);
    }

    /**
     * Constructs a graph with the specified directionality.
     *
     * @param isDirected {@code true} for a directed graph, {@code false} for undirected
     * <p><b>Time Complexity:</b> O(1)</p>
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
     * <p><b>Time Complexity:</b> O(1) amortized, O(V²) if matrix resizing is required</p>
     */
    @Override
    public void addEdge(K from, K to) {
        Objects.requireNonNull(from, "From-node cannot be null");
        Objects.requireNonNull(to, "To-node cannot be null");

        int u = getOrCreateIndex(from);
        int v = getOrCreateIndex(to);

        matrix[u][v] = true;
        if (!isDirected) matrix[v][u] = true;

        bridgeInfoValid = false;
    }

    /**
     * Removes an edge between two nodes if it exists. Does nothing if nodes not present.
     *
     * @param from the source node
     * @param to   the destination node
     * @throws NullPointerException if either node is null
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    @Override
    public void removeEdge(K from, K to) {
        Objects.requireNonNull(from, "From-node cannot be null");
        Objects.requireNonNull(to, "To-node cannot be null");

        Integer u = nodeIndexMap.get(from);
        Integer v = nodeIndexMap.get(to);
        if (u == null || v == null) return;

        matrix[u][v] = false;
        if (!isDirected) matrix[v][u] = false;

        bridgeInfoValid = false;
    }

    /**
     * Performs Breadth-First Search (BFS) starting from the given node.
     *
     * @param source the starting node
     * @param action a consumer to apply to each visited node
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public void bfs(K source, Consumer<K> action) {
        Integer idx = nodeIndexMap.get(source);
        if (idx == null) return;

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(idx);
        queue.offer(idx);

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
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public void dfs(K source, Consumer<K> action) {
        Integer idx = nodeIndexMap.get(source);
        if (idx == null) return;
        Set<Integer> visited = new HashSet<>();
        dfsHelper(idx, visited, action);
    }

    /**
     * Returns a topological ordering of the graph.
     * Valid only for directed acyclic graphs (DAGs).
     *
     * @return list of nodes in topological order
     * @throws UnsupportedOperationException if the graph is undirected
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
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
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public Map<K, Integer> shortestDistances(K source) {
        Map<K, Integer> dist = new HashMap<>();
        Integer idx = nodeIndexMap.get(source);
        if (idx == null) return dist;

        Queue<Integer> queue = new LinkedList<>();
        dist.put(source, 0);
        queue.offer(idx);

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
     * Checks whether there is a path from {@code from} to {@code to}.
     *
     * @param from source node
     * @param to   target node
     * @return {@code true} if reachable, else {@code false}
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public boolean isReachable(K from, K to) {
        Integer fromIdx = nodeIndexMap.get(from);
        Integer toIdx = nodeIndexMap.get(to);
        if (fromIdx == null || toIdx == null) return false;

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
     * Checks if the graph is connected.
     * In directed graphs, this checks reachable from the first added node.
     *
     * @return {@code true} if connected, {@code false} otherwise
     * <p><b>Time Complexity:</b> O(V²)</p>
     */
    @Override
    public boolean isConnected() {
        if (indexNodeList.isEmpty()) return true;
        Set<Integer> visited = new HashSet<>();
        dfsHelper(0, visited, k -> {});
        return visited.size() == indexNodeList.size();
    }

    /**
     * Checks if an edge is a bridge (i.e. its removal disconnects the graph).
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return {@code true} if it is a bridge
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    @Override
    public boolean isBridgeEdge(K u, K v) {
        computeBridgesIfNeeded();
        return cachedBridgeEdges.contains(new Edge<>(u, v, isDirected))
                || (!isDirected && cachedBridgeEdges.contains(new Edge<>(v, u, isDirected)));
    }

    /**
     * Returns all bridge edges in the graph.
     *
     * @return an unmodifiable set of bridge edges
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    @Override
    public Set<Edge<K>> getBridgeEdges() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeEdges);
    }

    /**
     * Returns all bridge nodes (articulation points).
     *
     * @return an unmodifiable set of bridge nodes
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    @Override
    public Set<K> getBridgeNodes() {
        computeBridgesIfNeeded();
        return Set.copyOf(cachedBridgeNodes);
    }

    /**
     * Checks if the node is a bridge node (articulation point).
     *
     * @param node the node to check
     * @return {@code true} if it is a bridge node
     * <p><b>Time Complexity:</b> O(1) after preprocessing</p>
     */
    @Override
    public boolean isBridgeNode(K node) {
        computeBridgesIfNeeded();
        return cachedBridgeNodes.contains(node);
    }

    // -- Internal utilities --

    private void ensureCapacity(int size) {
        if (size <= matrix.length) return;
        int newSize = Math.max(matrix.length * 2, size);
        boolean[][] newMatrix = new boolean[newSize][newSize];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, newMatrix[i], 0, matrix[i].length);
        }
        matrix = newMatrix;
    }

    private int getOrCreateIndex(K node) {
        return nodeIndexMap.computeIfAbsent(node, k -> {
            int idx = indexNodeList.size();
            indexNodeList.add(k);
            ensureCapacity(idx + 1);
            return idx;
        });
    }

    private void dfsHelper(int idx, Set<Integer> visited, Consumer<K> action) {
        visited.add(idx);
        action.accept(indexNodeList.get(idx));
        for (int i = 0; i < indexNodeList.size(); i++) {
            if (matrix[idx][i] && !visited.contains(i)) {
                dfsHelper(i, visited, action);
            }
        }
    }

    private void topoDfs(int idx, Set<Integer> visited, Deque<K> stack) {
        visited.add(idx);
        for (int i = 0; i < indexNodeList.size(); i++) {
            if (matrix[idx][i] && !visited.contains(i)) {
                topoDfs(i, visited, stack);
            }
        }
        stack.push(indexNodeList.get(idx));
    }

    private void computeBridgesIfNeeded() {
        if (bridgeInfoValid) return;

        int n = indexNodeList.size();
        int[] tin = new int[n], low = new int[n];
        boolean[] visited = new boolean[n];
        int[] time = {0};

        cachedBridgeEdges.clear();
        cachedBridgeNodes.clear();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsBridgeAndArticulation(i, -1, visited, tin, low, time);
            }
        }
        bridgeInfoValid = true;
    }

    private void dfsBridgeAndArticulation(int u, int parent, boolean[] visited,
                                          int[] tin, int[] low, int[] time) {
        visited[u] = true;
        tin[u] = low[u] = ++time[0];
        int children = 0;

        for (int v = 0; v < indexNodeList.size(); v++) {
            if (!matrix[u][v] || v == parent) continue;

            if (!visited[v]) {
                dfsBridgeAndArticulation(v, u, visited, tin, low, time);
                low[u] = Math.min(low[u], low[v]);

                if (low[v] > tin[u]) {
                    cachedBridgeEdges.add(new Edge<>(indexNodeList.get(u),
                            indexNodeList.get(v), isDirected));
                }
                if (parent != -1 && low[v] >= tin[u]) {
                    cachedBridgeNodes.add(indexNodeList.get(u));
                }
                children++;
            } else {
                low[u] = Math.min(low[u], tin[v]);
            }
        }

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
        for (int i = 0; i < indexNodeList.size(); i++) {
            System.out.print(indexNodeList.get(i) + " ");
            for (int j = 0; j < indexNodeList.size(); j++) {
                System.out.print(matrix[i][j] ? "T " : ". ");
            }
            System.out.println();
        }
    }
}
