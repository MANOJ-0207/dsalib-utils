package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AdjMatrixSimpleGraphTest {

    // ========== UNDIRECTED GRAPH TESTS ==========

    @Test
    void testUndirectedAddEdgeAndBFS() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        List<Integer> visited = new ArrayList<>();
        graph.bfs(1, visited::add);

        assertEquals(List.of(1, 2, 3), visited);
    }

    @Test
    void testUndirectedDFS() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(3, 4);

        List<Integer> visited = new ArrayList<>();
        graph.dfs(1, visited::add);

        assertTrue(visited.containsAll(List.of(1, 2, 3, 4)));
    }

    @Test
    void testUndirectedShortestDistances() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("C", "D");

        Map<String, Integer> dist = graph.shortestDistances("A");
        assertEquals(0, dist.get("A"));
        assertEquals(1, dist.get("B"));
        assertEquals(1, dist.get("C"));
        assertEquals(2, dist.get("D"));
    }

    @Test
    void testUndirectedReachability() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        assertTrue(graph.isReachable(1, 3));
        assertFalse(graph.isReachable(1, 4));
    }

    @Test
    void testUndirectedIsConnected() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        assertTrue(graph.isConnected());

        graph.addEdge(4, 5); // new disconnected component
        assertFalse(graph.isConnected());
    }

    @Test
    void testUndirectedBridgeEdgeDetection() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        assertTrue(graph.isBridgeEdge(2, 3));
        assertTrue(graph.isBridgeEdge(1, 2));
        assertFalse(graph.isBridgeEdge(1, 3));
        graph.addEdge(1, 3);
        assertFalse(graph.isBridgeEdge(2, 3));
        assertFalse(graph.isBridgeEdge(1, 2));
        assertFalse(graph.isBridgeEdge(1, 3));
    }

    @Test
    void testUndirectedBridgeNodeDetection() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        assertTrue(graph.isBridgeNode(2));
        assertFalse(graph.isBridgeNode(1));
        assertFalse(graph.isBridgeNode(3));
    }

    // ========== DIRECTED GRAPH TESTS ==========

    @Test
    void testDirectedAddEdgeAndBFS() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        List<String> visited = new ArrayList<>();
        graph.bfs("A", visited::add);

        assertEquals(List.of("A", "B", "C"), visited);
    }

    @Test
    void testDirectedDFS() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("C", "D");

        List<String> visited = new ArrayList<>();
        graph.dfs("A", visited::add);

        assertTrue(visited.containsAll(List.of("A", "B", "C", "D")));
    }

    @Test
    void testDirectedReachability() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("X", "Y");
        graph.addEdge("Y", "Z");

        assertTrue(graph.isReachable("X", "Z"));
        assertFalse(graph.isReachable("Z", "X"));
    }

    @Test
    void testDirectedShortestDistances() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        Map<String, Integer> dist = graph.shortestDistances("A");
        assertEquals(0, dist.get("A"));
        assertEquals(1, dist.get("B"));
        assertEquals(2, dist.get("C"));
    }

    @Test
    void testDirectedTopologicalSort() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        List<String> topo = graph.getTopologicalSort();
        assertEquals(List.of("A", "B", "C"), topo);
    }

    @Test
    void testDirectedTopologicalSortMultipleValid() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "C");
        graph.addEdge("B", "C");

        List<String> topo = graph.getTopologicalSort();

        assertTrue(
                List.of("A", "B", "C").equals(topo) ||
                        List.of("B", "A", "C").equals(topo)
        );
    }

    @Test
    void testDirectedTopologicalSortConstraints() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");

        List<String> result = graph.getTopologicalSort();

        assertTrue(isValidTopologicalOrder(result, Map.of(
                "A", List.of("B", "C"),
                "B", List.of("C")
        )));
    }

    @Test
    void testTopologicalSortThrowsForUndirected() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(false);
        graph.addEdge("A", "B");

        assertThrows(UnsupportedOperationException.class, graph::getTopologicalSort);
    }

    // ========== UTILITY METHOD ==========

    private boolean isValidTopologicalOrder(List<String> order, Map<String, List<String>> constraints) {
        Map<String, Integer> pos = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            pos.put(order.get(i), i);
        }

        for (Map.Entry<String, List<String>> entry : constraints.entrySet()) {
            String before = entry.getKey();
            for (String after : entry.getValue()) {
                if (pos.get(before) > pos.get(after)) return false;
            }
        }
        return true;
    }

    @Test
    void testRemoveEdgeUndirectedGraph() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.removeEdge(2, 3);

        assertFalse(graph.isReachable(1, 3));
        assertTrue(graph.isReachable(1, 2));
        assertFalse(graph.isBridgeEdge(2, 3));  // No longer exists
    }

    @Test
    void testRemoveEdgeDirectedGraph() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");

        graph.removeEdge("B", "C");

        assertTrue(graph.isReachable("A", "B"));
        assertFalse(graph.isReachable("A", "C"));
    }

    @Test
    void testRemoveSelfLoop() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 1); // self-loop
        graph.removeEdge(1, 1);

        // Still reachable from itself due to BFS logic
        assertTrue(graph.isReachable(1, 1));  // Graph should retain node
    }

    @Test
    void testRemoveEdgeThatNeverExisted() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);

        // No exception should be thrown
        graph.removeEdge(2, 3);

        assertTrue(graph.isReachable(1, 2));
        assertFalse(graph.isReachable(1, 3));
    }

    @Test
    void testRemoveEdgeDoesNotRemoveNodes() {
        AdjMatrixSimpleGraph<String> graph = new AdjMatrixSimpleGraph<>(true);
        graph.addEdge("X", "Y");
        graph.removeEdge("X", "Y");

        // Nodes should still be tracked internally
        assertTrue(graph.shortestDistances("X").containsKey("X"));
        assertFalse(graph.shortestDistances("X").containsKey("Y"));
    }

    @Test
    void testRemoveEdgeFromDisconnectedComponent() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(3, 4);

        graph.removeEdge(3, 4);
        assertTrue(graph.isReachable(1, 2));
        assertFalse(graph.isReachable(3, 4));
        assertFalse(graph.isReachable(1, 3));
    }

    @Test
    void testBridgeRecalculationAfterRemoval() {
        AdjMatrixSimpleGraph<Integer> graph = new AdjMatrixSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        assertTrue(graph.isBridgeEdge(1, 2));

        graph.removeEdge(1, 2);
        // Should no longer be considered a bridge
        assertFalse(graph.isBridgeEdge(1, 2));
    }

    @Test
    void testBridgeNodesDetectionInMatrixGraph() {
        AdjListSimpleGraph<Integer> graph = new AdjListSimpleGraph<>();
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);

        Set<Integer> expected = Set.of(2, 4, 5);
        Set<Integer> actual = graph.getBridgeNodes();

        assertEquals(expected, actual);
    }
}
