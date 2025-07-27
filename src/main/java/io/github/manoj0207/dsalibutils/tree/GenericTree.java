package io.github.manoj0207.dsalibutils.tree;

import io.github.manoj0207.dsalibutils.graph.disjointset.DisjointSet;

import java.util.*;
import java.util.function.Consumer;

/**
 * A generic tree or DAG (Directed Acyclic Graph) structure with:
 * - Safe edge addition (cycle prevention for undirected trees or DAGs)
 * - DFS and BFS traversal with customizable action
 * - Binary lifting for Lowest Common Ancestor (LCA) queries
 * - Topological sort (for DAGs)
 * - Internal auto-computation of metadata (depth, LCA, subtree size) for ease of use
 *
 * @param <K> the type of nodes in the tree/graph
 */
public class GenericTree<K> {
    private final Map<K, List<K>> adj = new HashMap<>();
    private final Map<K, K> parent = new HashMap<>();
    private final Map<K, Integer> depth = new HashMap<>();
    private final Map<K, Integer> subtreeSize = new HashMap<>();
    private final Map<K, List<K>> up = new HashMap<>();
    private final List<K> topoOrder = new ArrayList<>();

    private final DisjointSet<K> dsu = new DisjointSet<>();
    private final int MAX_LIFT;
    private final boolean directed;

    private boolean computationNeeded = true;
    private K rootNode = null;

    public GenericTree(boolean directed, int maxLift) {
        this.directed = directed;
        this.MAX_LIFT = maxLift;
    }

    /**
     * Adds an edge to the tree/graph. Prevents cycles automatically.
     *
     * @param u one endpoint of the edge
     * @param v the other endpoint of the edge
     * @return true if edge added successfully; false if it creates a cycle
     * @throws NullPointerException if u or v is null
     */
    public boolean addEdge(K u, K v) {
        Objects.requireNonNull(u);
        Objects.requireNonNull(v);
        computationNeeded = true;

        if (!directed) {
            if (!dsu.union(u, v)) return false;
            adj.computeIfAbsent(u, x -> new ArrayList<>()).add(v);
            adj.computeIfAbsent(v, x -> new ArrayList<>()).add(u);
        } else {
            adj.computeIfAbsent(u, x -> new ArrayList<>()).add(v);
            if (hasCycleDFS()) {
                adj.get(u).remove(v);
                return false;
            }
        }

        if (rootNode == null) rootNode = u;
        return true;
    }

    /**
     * Removes an edge between two nodes if allowed.
     * For undirected trees, edge removal is only permitted if at least one of the nodes is a leaf
     * (i.e., has only one connection), to avoid disconnecting the tree.
     *
     * @param u one endpoint
     * @param v the other endpoint
     * @return true if the edge was removed; false if it didn't exist or removal is unsafe
     */
    public boolean removeEdge(K u, K v) {
        List<K> listU = adj.get(u);
        List<K> listV = adj.get(v);
        if (listU == null || listV == null) return false;
        if (!listU.contains(v) && !listV.contains(u)) return false;

        // Only allow removal if either u or v is a leaf
        if (listU.size() > 1 && listV.size() > 1) return false;

        // Remove edge safely
        listU.remove(v);
        listV.remove(u);
        computationNeeded = true;

        // Cleanup if node becomes completely disconnected
        if (listU.isEmpty()) adj.remove(u);
        if (listV.isEmpty()) adj.remove(v);

        // Handle root reassignment
        if (Objects.equals(rootNode, u)) {
            rootNode = !listV.isEmpty() ? v : null;
        } else if (Objects.equals(rootNode, v)) {
            rootNode = !listU.isEmpty() ? u : null;
        }

        return true;
    }


    private void dfsForConnectivity(K node, Set<K> visited) {
        visited.add(node);
        for (K nei : adj.getOrDefault(node, List.of())) {
            if (!visited.contains(nei)) {
                dfsForConnectivity(nei, visited);
            }
        }
    }



    /**
     * Performs DFS from the given root with a consumer action.
     *
     * @param root the starting node
     * @param action action to apply to each visited node
     */
    public void dfs(K root, Consumer<K> action) {
        dfs(root, null, action);
    }

    private void dfs(K node, K par, Consumer<K> action) {
        parent.put(node, par);
        depth.put(node, par == null ? 0 : depth.get(par) + 1);
        subtreeSize.put(node, 1);
        action.accept(node);

        for (K nei : adj.getOrDefault(node, List.of())) {
            if (!nei.equals(par)) {
                dfs(nei, node, action);
                subtreeSize.put(node, subtreeSize.get(node) + subtreeSize.get(nei));
            }
        }
    }

    /**
     * Performs BFS from the given source node.
     *
     * @param source the starting node
     * @param action action to apply to each visited node
     */
    public void bfs(K source, Consumer<K> action) {
        if (!adj.containsKey(source)) return;

        Set<K> visited = new HashSet<>();
        Queue<K> queue = new LinkedList<>();
        visited.add(source);
        queue.offer(source);

        while (!queue.isEmpty()) {
            K curr = queue.poll();
            action.accept(curr);
            for (K nei : adj.getOrDefault(curr, List.of())) {
                if (visited.add(nei)) queue.offer(nei);
            }
        }
    }

    /**
     * Ensures that DFS and LCA lifting table are built if needed.
     */
    private void ensureComputed() {
        if (computationNeeded && rootNode != null) {
            parent.clear(); depth.clear(); subtreeSize.clear(); up.clear();
            dfs(rootNode, x -> {});
            buildLifting(rootNode);
            computationNeeded = false;
        }
    }

    /**
     * Builds binary lifting table for LCA computation.
     *
     * @param node the current node to start building from
     */
    public void buildLifting(K node) {
        computeBinaryLifting(node, parent.get(node));
    }

    private void computeBinaryLifting(K node, K par) {
        List<K> table = new ArrayList<>(Collections.nCopies(MAX_LIFT, null));
        table.set(0, par);

        for (int i = 1; i < MAX_LIFT; i++) {
            K prev = table.get(i - 1);
            if (prev != null && up.containsKey(prev)) {
                table.set(i, up.get(prev).get(i - 1));
            }
        }

        up.put(node, table);

        for (K nei : adj.getOrDefault(node, List.of())) {
            if (!nei.equals(par)) {
                computeBinaryLifting(nei, node);
            }
        }
    }

    private K lift(K node, int dist) {
        for (int i = 0; i < MAX_LIFT; i++) {
            if (((dist >> i) & 1) == 1) {
                List<K> table = up.get(node);
                if (table == null || table.get(i) == null) return null;
                node = table.get(i);
            }
        }
        return node;
    }

    /**
     * Returns the lowest common ancestor of two nodes.
     *
     * @param u first node
     * @param v second node
     * @return the LCA node, or null if one is unreachable
     */
    public K getLCA(K u, K v) {
        ensureComputed();

        Integer du = depth.get(u), dv = depth.get(v);
        if (du == null || dv == null) return null;

        if (du < dv) {
            K temp = u; u = v; v = temp;
            int tmp = du; du = dv; dv = tmp;
        }

        u = lift(u, du - dv);
        if (u == null || u.equals(v)) return u;

        for (int i = MAX_LIFT - 1; i >= 0; i--) {
            List<K> upU = up.get(u), upV = up.get(v);
            if (upU != null && upV != null && upU.get(i) != null && !upU.get(i).equals(upV.get(i))) {
                u = upU.get(i);
                v = upV.get(i);
            }
        }

        return parent.get(u);
    }

    /**
     * Returns depth of a node.
     *
     * @param node the node
     * @return depth if available, -1 otherwise
     */
    public int getDepth(K node) {
        ensureComputed();
        return depth.getOrDefault(node, -1);
    }

    /**
     * Returns the size of the subtree rooted at the given node.
     *
     * @param node the root node
     * @return size of subtree or 0 if unknown
     */
    public int getSubtreeSize(K node) {
        ensureComputed();
        return subtreeSize.getOrDefault(node, 0);
    }

    /**
     * Returns a topological order of the DAG (only valid for directed graphs).
     *
     * @return a list of nodes in topological order
     */
    public List<K> getTopologicalOrder() {
        if (!directed) throw new UnsupportedOperationException("Topological sort only valid for DAGs");
        topoOrder.clear();
        Set<K> visited = new HashSet<>();
        for (K node : adj.keySet()) {
            if (!visited.contains(node)) {
                topoDFS(node, visited);
            }
        }
        List<K> result = new ArrayList<>(topoOrder);
        Collections.reverse(result);
        return result;
    }

    private void topoDFS(K node, Set<K> visited) {
        visited.add(node);
        for (K nei : adj.getOrDefault(node, List.of())) {
            if (!visited.contains(nei)) {
                topoDFS(nei, visited);
            }
        }
        topoOrder.add(node);
    }

    /**
     * Prints the adjacency list (mainly for debugging purposes).
     */
    public void printAdjacencyList() {
        for (Map.Entry<K, List<K>> entry : adj.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    // ---------------- Internal Utilities ------------------

    private boolean hasCycleDFS() {
        Set<K> visited = new HashSet<>();
        Set<K> inStack = new HashSet<>();

        for (K node : adj.keySet()) {
            if (!visited.contains(node)) {
                if (dfsDirectedCycle(node, visited, inStack)) return true;
            }
        }
        return false;
    }

    private boolean dfsDirectedCycle(K node, Set<K> visited, Set<K> inStack) {
        visited.add(node);
        inStack.add(node);

        for (K nei : adj.getOrDefault(node, List.of())) {
            if (!visited.contains(nei)) {
                if (dfsDirectedCycle(nei, visited, inStack)) return true;
            } else if (inStack.contains(nei)) {
                return true;
            }
        }

        inStack.remove(node);
        return false;
    }
}
