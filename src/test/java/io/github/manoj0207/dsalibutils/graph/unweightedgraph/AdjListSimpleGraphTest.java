package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AdjListSimpleGraphTest {

    // === UNDIRECTED GRAPH TESTS ===

    @Test
    void testAddAndBfsUndirected() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        List<Integer> visited = new ArrayList<>();
        graph.bfs(1, visited::add);

        assertEquals(List.of(1, 2, 3), visited);
    }

    @Test
    void testDfsTraversalUndirected() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);

        Set<Integer> visited = new LinkedHashSet<>();
        graph.dfs(1, visited::add);

        assertTrue(visited.containsAll(List.of(1, 2, 4, 3)));
    }

    @Test
    void testShortestDistancesUndirected() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");

        Map<String, Integer> dist = graph.shortestDistances("A");
        assertEquals(0, dist.get("A"));
        assertEquals(1, dist.get("B"));
        assertEquals(1, dist.get("C"));
        assertEquals(2, dist.get("D"));
    }

    @Test
    void testReachabilityUndirected() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        assertTrue(graph.isReachable(1, 3));
        assertFalse(graph.isReachable(1, 4));
    }

    @Test
    void testIsConnectedForUndirected() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        assertTrue(graph.isConnected());

        graph.addEdge(4, 5);
        assertFalse(graph.isConnected());
    }

    @Test
    void testBridgeEdgeAndNodeDetection() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        assertTrue(graph.isBridgeEdge(2, 3));
        assertTrue(graph.isBridgeEdge(3, 2));

        assertTrue(graph.isBridgeNode(2));
        assertTrue(graph.isBridgeNode(3));
        assertFalse(graph.isBridgeNode(1));
    }

    @Test
    void testGetAllBridgeEdges() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(3, 4);

        Set<Edge<Integer>> bridges = graph.getBridgeEdges();
        assertTrue(bridges.contains(new Edge<>(1, 2, false)) || bridges.contains(new Edge<>(2, 1, false)));
        assertTrue(bridges.contains(new Edge<>(3, 4, false)) || bridges.contains(new Edge<>(4, 3, false)));
    }

    // === DIRECTED GRAPH TESTS ===

    @Test
    void testTopologicalSortSimpleChain() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        List<String> result = graph.getTopologicalSort();
        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    void testTopologicalSortMultipleValidOrders() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("A", "C");
        graph.addEdge("B", "C");

        List<String> result = graph.getTopologicalSort();
        assertTrue(List.of("A", "B", "C").equals(result) || List.of("B", "A", "C").equals(result));
    }

    @Test
    void testComplexTopologicalSortWithConstraints() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("A", "C");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");

        List<String> result = graph.getTopologicalSort();

        assertTrue(isValidTopologicalOrder(result, Map.of(
                "A", List.of("C"),
                "B", List.of("C"),
                "C", List.of("D")
        )));
    }

    @Test
    void testTopologicalSortThrowsForUndirected() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(false);
        graph.addEdge("A", "B");

        assertThrows(UnsupportedOperationException.class, graph::getTopologicalSort);
    }

    @Test
    void testDirectedGraphBfs() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");

        List<String> visited = new ArrayList<>();
        graph.bfs("A", visited::add);

        assertEquals(List.of("A", "B", "C", "D"), visited);
    }

    @Test
    void testDirectedGraphReachability() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        assertTrue(graph.isReachable("A", "C"));
        assertFalse(graph.isReachable("C", "A"));
    }

    @Test
    void testDirectedGraphDfs() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);
        graph.addEdge("X", "Y");
        graph.addEdge("Y", "Z");

        Set<String> visited = new LinkedHashSet<>();
        graph.dfs("X", visited::add);

        assertEquals(Set.of("X", "Y", "Z"), visited);
    }

    // === UTILITY ===

    private boolean isValidTopologicalOrder(List<String> order, Map<String, List<String>> constraints) {
        Map<String, Integer> position = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }

        for (Map.Entry<String, List<String>> entry : constraints.entrySet()) {
            for (String dependent : entry.getValue()) {
                if (position.get(entry.getKey()) >= position.get(dependent)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    void testRemoveEdgeUndirectedGraph() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);

        // Remove existing edge
        graph.removeEdge(2, 3);

        // 2 should no longer be connected to 3
        assertFalse(graph.isReachable(2, 4));
        assertFalse(graph.isReachable(4, 2));

        // 1 and 2 should still be connected
        assertTrue(graph.isReachable(1, 2));

        // Remove again (no effect, should not throw)
        graph.removeEdge(2, 3);
        graph.removeEdge(3, 2);

        // Edge removal should not affect other edges
        assertTrue(graph.isReachable(1, 2));
    }

    @Test
    void testRemoveEdgeDirectedGraph() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>(true);

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        // Remove edge A -> B
        graph.removeEdge("A", "B");

        assertFalse(graph.isReachable("A", "B"));
        assertFalse(graph.isReachable("A", "C"));

        // B -> C should still exist
        assertTrue(graph.isReachable("B", "C"));

        // Remove non-existent edge
        graph.removeEdge("A", "C");
        graph.removeEdge("C", "A"); // Directed: not even symmetric
    }

    @Test
    void testRemoveEdgeDoesNotDeleteNode() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.removeEdge(1, 2);

        // Node 1 and 2 should still exist in adjacencyList (possibly with empty lists)
        assertNotNull(graph.getAdjacencyList().get(1));
        assertNotNull(graph.getAdjacencyList().get(2));
    }

    @Test
    void testRemoveEdgeCornerCases() {
        AdjListSimpleGraph<String> graph = new AdjListSimpleGraph<>();

        // Try removing before any edge is added
        graph.removeEdge("X", "Y"); // should not throw

        // Add then remove
        graph.addEdge("X", "Y");
        graph.removeEdge("X", "Y");

        assertFalse(graph.isReachable("X", "Y"));
        assertFalse(graph.isReachable("Y", "X")); // undirected

        // Add more edges and remove one
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.removeEdge("A", "B");

        assertFalse(graph.isReachable("A", "B"));
        assertTrue(graph.isReachable("A", "C"));
    }

    @Test
    void testBridgeNodesDetection() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);

        // The articulation points should be 2, 4, and 5
        Set<Integer> expected = Set.of(2, 4, 5);
        Set<Integer> actual = graph.getBridgeNodes();

        assertEquals(expected, actual);
    }


}
