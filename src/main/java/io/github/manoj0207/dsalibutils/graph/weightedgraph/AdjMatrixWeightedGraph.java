package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import io.github.manoj0207.dsalibutils.graph.disjointset.DisjointSet;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.DetailedEdge;
import io.github.manoj0207.dsalibutils.graph.weightedgraph.edge.WeightedEdge;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a weighted graph using an adjacency matrix.
 * Supports both directed and undirected graphs.
 *
 * @param <K> the type of nodes in the graph
 */
public class AdjMatrixWeightedGraph<K> implements WeightedGraph<K>{
    private final Map<K, Integer> nodeIndexMap = new HashMap<>();
    private final List<K> indexNodeList = new ArrayList<>();
    private final List<DetailedEdge<K>> allEdges = new ArrayList<>();
    private int[][] matrix;
    private final boolean isDirected;
    private int size = 0;

    /**
     * Constructs an empty graph.
     *
     * @param isDirected true for directed, false for undirected
     */
    public AdjMatrixWeightedGraph(boolean isDirected) {
        this.isDirected = isDirected;
        this.matrix = new int[10][10];
        for (int[] row : matrix) Arrays.fill(row, Integer.MAX_VALUE);
    }

    /** Ensures internal matrix capacity is sufficient. */
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

    /** Registers a node if it's not already present. */
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
     * Adds an edge from {@code from} to {@code to} with the given {@code weight}.
     */
    @Override
    public void addEdge(K from, K to, int weight) {
        registerNode(from);
        registerNode(to);
        int u = nodeIndexMap.get(from);
        int v = nodeIndexMap.get(to);
        matrix[u][v] = weight;
        if (!isDirected) matrix[v][u] = weight;
        allEdges.add(new DetailedEdge<>(from, to, weight));
    }

    /**
     * Removes the edge between {@code from} and {@code to}.
     */
    @Override
    public void removeEdge(K from, K to) {
        if (!nodeIndexMap.containsKey(from) || !nodeIndexMap.containsKey(to)) return;
        int u = nodeIndexMap.get(from);
        int v = nodeIndexMap.get(to);
        matrix[u][v] = Integer.MAX_VALUE;
        if (!isDirected) matrix[v][u] = Integer.MAX_VALUE;
        allEdges.removeIf(e -> (e.source().equals(from) && e.dest().equals(to)) ||
                (!isDirected && e.source().equals(to) && e.dest().equals(from)));
    }

    /**
     * Performs BFS traversal from the starting node.
     */
    public void bfs(K start) {
        bfs(start, k -> System.out.print(k + " "));
        System.out.println();
    }

    /**
     * Performs BFS traversal and applies action on each node.
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
     * Performs DFS traversal from the starting node.
     */
    public void dfs(K start) {
        dfs(start, k -> System.out.print(k + " "));
        System.out.println();
    }

    /**
     * Performs DFS traversal and applies action on each node.
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
     * Checks if {@code to} is reachable from {@code from}.
     *
     * @return true if reachable, false otherwise
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
     * Dijkstra’s shortest path algorithm.
     *
     * @return shortest path distance or -1 if unreachable
     */
    @Override
    public int dijkstra(K source, K destination) {
        if (!nodeIndexMap.containsKey(source) || !nodeIndexMap.containsKey(destination)) return -1;
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
        return d == Integer.MAX_VALUE ? -1 : d;
    }

    /**
     * Bellman-Ford algorithm to compute shortest paths from source.
     *
     * @throws IllegalStateException if a negative cycle exists
     */
    @Override
    public Map<K, Integer> bellmanFord(K source) {
        Map<K, Integer> dist = new HashMap<>();
        for (K node : nodeIndexMap.keySet()) dist.put(node, Integer.MAX_VALUE);
        dist.put(source, 0);

        for (int i = 0; i < size - 1; i++) {
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
                throw new IllegalStateException("Negative weight cycle detected");
            }
        }

        return dist;
    }

    /**
     * Computes the Minimum Spanning Tree using Prim's algorithm.
     */
    @Override
    public int primsMST(K start) {
        if (!nodeIndexMap.containsKey(start)) return -1;
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
     * Computes the MST using Kruskal's algorithm (valid only for undirected graphs).
     */
    @Override
    public int kruskalMST() {
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
     * Computes all-pairs shortest paths using Floyd-Warshall algorithm.
     *
     * @return distance matrix
     */
    public Map<K, Map<K, Integer>> floydWarshall() {
        Map<K, Map<K, Integer>> dist = new HashMap<>();
        for (K i : nodeIndexMap.keySet()) {
            dist.put(i, new HashMap<>());
            for (K j : nodeIndexMap.keySet()) {
                if (i.equals(j)) dist.get(i).put(j, 0);
                else dist.get(i).put(j, Integer.MAX_VALUE);
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
     * Throws exception for SCC on undirected graph, as it's not meaningful.
     */
    @Override
    public List<List<K>> getStronglyConnectedComponents() {
        if (!isDirected) {
            throw new UnsupportedOperationException("SCCs are only defined for directed graphs.");
        }

        boolean[] visited = new boolean[size];
        Deque<Integer> stack = new ArrayDeque<>();

        // Step 1: Fill vertices in stack according to finishing times
        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                fillOrder(i, visited, stack);
            }
        }

        // Step 2: Transpose the graph
        boolean[][] transposed = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] != Integer.MAX_VALUE) {
                    transposed[j][i] = true;
                }
            }
        }

        // Step 3: Process all vertices in order defined by stack
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



    @Override
    public boolean isStronglyConnected() {
        if (!isDirected) {
            // For undirected, check if graph is connected via BFS
            boolean[] visited = new boolean[size];
            dfsHelper(indexNodeList.get(0), visited, k -> {});
            for (boolean b : visited) {
                if (!b) return false;
            }
            return true;
        } else {
            // For directed graph, check reachability from every node to every other node
            for (K from : indexNodeList) {
                Set<K> reachable = new HashSet<>();
                boolean[] visited = new boolean[size];
                dfsHelper(from, visited, reachable::add);
                if (reachable.size() != size) return false;
            }
            return true;
        }
    }

    @Override
    public int getSCCCount() {
        return getStronglyConnectedComponents().size();
    }

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
     * Prints the matrix for debugging.
     */
    @Override
    public void printGraph() {
        System.out.print("  ");
        for (K node : indexNodeList) System.out.print(node + " ");
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
