package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AdjMatrixWeightedGraphTest {

    private WeightedGraph<String> directedGraph;
    private WeightedGraph<String> undirectedGraph;
    private WeightedGraph<Integer> intGraph;

    @BeforeEach
    void setUp() {
        directedGraph = new AdjMatrixWeightedGraph<>(true);
        undirectedGraph = new AdjMatrixWeightedGraph<>(false);
        intGraph = new AdjMatrixWeightedGraph<>(false);
    }

    @Test
    void testAddAndRemoveEdge() {
        undirectedGraph.addEdge("A", "B", 4);
        undirectedGraph.addEdge("B", "C", 2);
        assertEquals(6, undirectedGraph.dijkstra("A", "C")); // A->B->C

        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeEdge("B", "C"));

        undirectedGraph.removeEdge("B", "C");
        assertNull(undirectedGraph.dijkstra("A", "C")); // disconnected
    }

    @Test
    void testOverwriteEdge() {
        undirectedGraph.addEdge("A", "B", 10);
        undirectedGraph.addEdge("A", "B", 2);
        assertEquals(2, undirectedGraph.dijkstra("A", "B")); // updated weight
    }

    @Test
    void testRemoveEdgeThatDoesNotExist() {
        undirectedGraph.addEdge("A", "B", 3);
        undirectedGraph.removeEdge("B", "C"); // should not throw
        assertEquals(3, undirectedGraph.dijkstra("A", "B"));
    }

    @Test
    void testDijkstra() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 2);
        directedGraph.addEdge("A", "C", 10);

        assertEquals(3, directedGraph.dijkstra("A", "C"));
        assertEquals(1, directedGraph.dijkstra("A", "B"));
        assertNull(directedGraph.dijkstra("C", "A")); // unreachable
    }

    @Test
    void testBellmanFord() {
        directedGraph.addEdge("A", "B", 4);
        directedGraph.addEdge("B", "C", -2);
        directedGraph.addEdge("C", "D", 3);
        directedGraph.addEdge("A", "D", 10);

        Map<String, Integer> dist = directedGraph.bellmanFord("A");
        assertEquals(4, dist.get("B"));
        assertEquals(2, dist.get("C"));
        assertEquals(5, dist.get("D"));
    }

    @Test
    void testBellmanFordNegativeCycle() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", -2);
        directedGraph.addEdge("C", "A", -2);

        assertThrows(IllegalStateException.class, () -> directedGraph.bellmanFord("A"));
    }

    @Test
    void testNegativeWeightHandled() {
        directedGraph.addEdge("A", "B", 4);
        directedGraph.addEdge("B", "C", -2);
        directedGraph.addEdge("A", "C", 5);

        Map<String, Integer> dist = directedGraph.bellmanFord("A");
        assertEquals(2, dist.get("C")); // A->B->C
    }

    @Test
    void testPrimsMST() {
        intGraph.addEdge(0, 1, 10);
        intGraph.addEdge(1, 2, 5);
        intGraph.addEdge(0, 2, 1);

        int totalWeight = intGraph.primsMST(0);
        assertEquals(6, totalWeight); // 0–2 (1), 2–1 (5)
    }

    @Test
    void testKruskalsMST() {
        undirectedGraph.addEdge("A", "B", 5);
        undirectedGraph.addEdge("A", "C", 3);
        undirectedGraph.addEdge("B", "C", 1);

        int mst = undirectedGraph.kruskalMST();
        assertEquals(4, mst); // B–C (1), A–C (3)
    }

    @Test
    void testDisconnectedGraphMST() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("C", "D", 2);

        int mst = undirectedGraph.kruskalMST();
        assertEquals(3, mst); // two components
    }

    @Test
    void testFloydWarshall() {
        undirectedGraph.addEdge("A", "B", 2);
        undirectedGraph.addEdge("B", "C", 3);
        undirectedGraph.addEdge("A", "C", 10);

        Map<String, Map<String, Integer>> dist = undirectedGraph.floydWarshall();
        assertEquals(5, dist.get("A").get("C")); // A->B->C
        assertEquals(2, dist.get("A").get("B"));
        assertEquals(3, dist.get("B").get("C"));
        assertEquals(0, dist.get("A").get("A"));
    }

    @Test
    void testFloydWarshallDisconnected() {
        undirectedGraph.addEdge("A", "B", 3);
        undirectedGraph.addEdge("C", "D", 5);

        Map<String, Map<String, Integer>> dist = undirectedGraph.floydWarshall();
        assertEquals(Integer.MAX_VALUE, dist.get("A").get("D")); // disconnected
    }

    @Test
    void testDFSAndBFS() {
        undirectedGraph.addEdge("X", "Y", 1);
        undirectedGraph.addEdge("A", "B", 1);

        List<String> bfs = new ArrayList<>();
        undirectedGraph.bfs("X", bfs::add);
        assertTrue(bfs.containsAll(List.of("X", "Y")));
        assertFalse(bfs.contains("A"));

        List<String> dfs = new ArrayList<>();
        undirectedGraph.dfs("A", dfs::add);
        assertTrue(dfs.containsAll(List.of("A", "B")));
        assertFalse(dfs.contains("X"));

        undirectedGraph.addEdge("Y", "A", 1);
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeEdge("X", "Y"));
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeNode("Y"));

    }

    @Test
    void testBFSDirected() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("A", "C", 1);
        directedGraph.addEdge("C", "D", 1);

        List<String> visited = new ArrayList<>();
        directedGraph.bfs("A", visited::add);
        assertTrue(visited.containsAll(List.of("A", "B", "C", "D")));
    }

    @Test
    void testDFSDirected() {
        directedGraph.addEdge("X", "Y", 1);
        directedGraph.addEdge("Y", "Z", 1);
        directedGraph.addEdge("Z", "W", 1);

        List<String> visited = new ArrayList<>();
        directedGraph.dfs("X", visited::add);
        assertEquals(List.of("X", "Y", "Z", "W"), visited);
    }

    @Test
    void testReachabilityDirected() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);
        directedGraph.addEdge("D", "E", 1);

        assertTrue(directedGraph.isReachable("A", "C"));
        assertFalse(directedGraph.isReachable("C", "A"));
        assertFalse(directedGraph.isReachable("A", "E"));
    }

    @Test
    void testReachabilityUndirected() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("C", "D", 1);
        undirectedGraph.addEdge("B", "C", 1);
        assertTrue(undirectedGraph.isReachable("B", "A"));
        assertTrue(undirectedGraph.isReachable("A", "D"));
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeEdge("A", "B"));
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeNode("B"));
    }

    @Test
    void testSelfLoop() {
        directedGraph.addEdge("A", "A", 7);
        assertEquals(0, directedGraph.dijkstra("A", "A")); // self-loop treated as zero
        assertTrue(directedGraph.isReachable("A", "A"));
    }

    @Test
    void testIsStronglyConnectedDirectedTrue() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);
        directedGraph.addEdge("C", "A", 1);

        assertTrue(directedGraph.isStronglyConnected());
    }

    @Test
    void testIsStronglyConnectedDirectedFalse() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);

        assertFalse(directedGraph.isStronglyConnected());
    }

    @Test
    void testIsStronglyConnectedUndirectedTrue() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("B", "C", 1);
        undirectedGraph.addEdge("C", "A", 1);

        assertTrue(undirectedGraph.isStronglyConnected());

        assertFalse(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeEdge("A", "B"));
        assertFalse(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeNode("B"));
    }

    @Test
    void testIsStronglyConnectedUndirectedFalse() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("C", "D", 1);

        assertFalse(undirectedGraph.isStronglyConnected());

        undirectedGraph.addEdge("B", "C", 1);
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeEdge("A", "B"));

        assertTrue(undirectedGraph.isStronglyConnected());
        assertTrue(((AdjMatrixWeightedGraph<String>) undirectedGraph).isBridgeNode("B"));
    }

    @Test
    void testUnsupportedSCCMethods() {
        assertThrows(UnsupportedOperationException.class, undirectedGraph::getStronglyConnectedComponents);
        assertThrows(UnsupportedOperationException.class, undirectedGraph::getSCCCount);
        assertThrows(UnsupportedOperationException.class, undirectedGraph::getSCCMap);
    }

    @Test
    void testValidSCCDetails() {
        AdjMatrixWeightedGraph<String> graph = new AdjMatrixWeightedGraph<>(true);
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("C", "A", 1);
        graph.addEdge("C", "D", 1);
        graph.addEdge("D", "E", 1);

        List<List<String>> sccs = graph.getStronglyConnectedComponents();
        assertEquals(3, sccs.size());

        Map<String, Integer> sccMap = graph.getSCCMap();
        int abcComponent = sccMap.get("A");
        assertEquals(abcComponent, sccMap.get("B"));
        assertEquals(abcComponent, sccMap.get("C"));
        assertNotEquals(abcComponent, sccMap.get("D"));
        assertNotEquals(sccMap.get("D"), sccMap.get("E"));

        assertEquals(3, graph.getSCCCount());
    }

    @Test
    void testPrintGraphNoException() {
        undirectedGraph.addEdge("A", "B", 2);
        assertDoesNotThrow(undirectedGraph::printGraph); // visual check only
    }

    @Test
    void testEnsureCapacityAndLargeGraph() {
        AdjMatrixWeightedGraph<Integer> largeGraph = new AdjMatrixWeightedGraph<>(false);
        for (int i = 0; i < 20; i++) {
            largeGraph.addEdge(i, (i + 1) % 20, 1); // triggers internal resizing
        }
        assertEquals(1, largeGraph.dijkstra(0, 1));
    }
}
