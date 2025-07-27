package io.github.manoj0207.dsalibutils.graph.weightedgraph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AdjListWeightedGraphTest {

    private WeightedGraph<String> directedGraph;
    private WeightedGraph<String> undirectedGraph;
    private WeightedGraph<Integer> undirectedIntGraph;

    @BeforeEach
    void setUp() {
        directedGraph = new AdjListWeightedGraph<>(true);
        undirectedGraph = new AdjListWeightedGraph<>(false);
        undirectedIntGraph = new AdjListWeightedGraph<>(false);
    }

    @Test
    void testAddAndRemoveEdge() {
        undirectedGraph.addEdge("A", "B", 5);
        undirectedGraph.addEdge("B", "C", 3);

        assertTrue(undirectedGraph.isReachable("A", "C"));
        undirectedGraph.removeEdge("B", "C");
        assertFalse(undirectedGraph.isReachable("A", "C"));
    }

    @Test
    void testDijkstra() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 2);
        directedGraph.addEdge("A", "C", 10);

        assertEquals(3, directedGraph.dijkstra("A", "C"));
        assertEquals(1, directedGraph.dijkstra("A", "B"));
        assertEquals(-1, directedGraph.dijkstra("C", "A"));
    }

    @Test
    void testDijkstraInvalidSourceOrDestination() {
        directedGraph.addEdge("X", "Y", 5);
        assertEquals(-1, directedGraph.dijkstra("A", "B")); // nonexistent nodes
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
        directedGraph.addEdge("B", "C", -1);
        directedGraph.addEdge("C", "A", -1);

        assertThrows(IllegalStateException.class, () -> directedGraph.bellmanFord("A"));
    }

    @Test
    void testBellmanFordInvalidSource() {
        assertThrows(IllegalArgumentException.class, () -> directedGraph.bellmanFord("X"));
    }

    @Test
    void testPrimsMST() {
        undirectedIntGraph.addEdge(0, 1, 10);
        undirectedIntGraph.addEdge(1, 2, 5);
        undirectedIntGraph.addEdge(0, 2, 1);

        int mstWeight = undirectedIntGraph.primsMST(0);
        assertEquals(6, mstWeight);
    }

    @Test
    void testKruskalsMST() {
        undirectedGraph.addEdge("A", "B", 4);
        undirectedGraph.addEdge("A", "C", 1);
        undirectedGraph.addEdge("C", "B", 2);

        int mstWeight = undirectedGraph.kruskalMST();
        assertEquals(3, mstWeight);
    }

    @Test
    void testDFSAndBFSUndirected() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("A", "C", 1);
        undirectedGraph.addEdge("B", "D", 1);

        List<String> dfsOrder = new ArrayList<>();
        undirectedGraph.dfs("A", dfsOrder::add);

        List<String> bfsOrder = new ArrayList<>();
        undirectedGraph.bfs("A", bfsOrder::add);

        assertTrue(dfsOrder.containsAll(List.of("A", "B", "C", "D")));
        assertTrue(bfsOrder.containsAll(List.of("A", "B", "C", "D")));
        assertEquals("A", bfsOrder.get(0));
    }

    @Test
    void testDFSAndBFSDirected() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("A", "C", 1);
        directedGraph.addEdge("B", "D", 1);

        List<String> dfsOrder = new ArrayList<>();
        directedGraph.dfs("A", dfsOrder::add);

        List<String> bfsOrder = new ArrayList<>();
        directedGraph.bfs("A", bfsOrder::add);

        assertTrue(dfsOrder.containsAll(List.of("A", "B", "C", "D")));
        assertTrue(bfsOrder.containsAll(List.of("A", "B", "C", "D")));
    }

    @Test
    void testDFSUnreachableInDirected() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);
        directedGraph.addEdge("D", "E", 1);

        List<String> dfs = new ArrayList<>();
        directedGraph.dfs("A", dfs::add);

        assertTrue(dfs.contains("A"));
        assertFalse(dfs.contains("D"));
    }

    @Test
    void testReachabilityUndirected() {
        undirectedGraph.addEdge("1", "2", 1);
        undirectedGraph.addEdge("3", "4", 1);

        assertTrue(undirectedGraph.isReachable("1", "2"));
        assertTrue(undirectedGraph.isReachable("2", "1"));
        assertFalse(undirectedGraph.isReachable("1", "3"));
    }

    @Test
    void testBFSOrderUndirected() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("A", "C", 1);
        undirectedGraph.addEdge("B", "D", 1);

        List<String> bfsOrder = new ArrayList<>();
        undirectedGraph.bfs("A", bfsOrder::add);

        assertEquals("A", bfsOrder.get(0));
        assertTrue(bfsOrder.containsAll(List.of("B", "C", "D")));
    }

    @Test
    void testSCCDirected() {
        directedGraph.addEdge("A", "B", 1);
        directedGraph.addEdge("B", "C", 1);
        directedGraph.addEdge("C", "A", 1);
        directedGraph.addEdge("C", "D", 1);

        List<List<String>> scc = directedGraph.getStronglyConnectedComponents();
        assertEquals(2, scc.size());

        Map<String, Integer> sccMap = directedGraph.getSCCMap();
        assertEquals(sccMap.get("A"), sccMap.get("B"));
        assertNotEquals(sccMap.get("A"), sccMap.get("D"));
    }

    @Test
    void testSCCUnsupportedForUndirected() {
        undirectedGraph.addEdge("A", "B", 1);
        undirectedGraph.addEdge("B", "C", 1);

        assertThrows(UnsupportedOperationException.class, undirectedGraph::getStronglyConnectedComponents);
        assertThrows(UnsupportedOperationException.class, undirectedGraph::getSCCMap);
        assertThrows(UnsupportedOperationException.class, undirectedGraph::isStronglyConnected);
    }

    @Test
    void testIsReachable() {
        directedGraph.addEdge("X", "Y", 1);
        directedGraph.addEdge("Y", "Z", 1);

        assertTrue(directedGraph.isReachable("X", "Z"));
        assertFalse(directedGraph.isReachable("Z", "X"));
    }

    @Test
    void testEmptyGraphReachability() {
        assertFalse(directedGraph.isReachable("A", "B"));
    }
}
