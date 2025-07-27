package io.github.manoj0207.dsalibutils.tree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GenericTree}.
 */
public class GenericTreeTest {

    private GenericTree<Integer> undirectedTree;
    private GenericTree<Integer> dag;

    @BeforeEach
    public void setup() {
        undirectedTree = new GenericTree<>(false, 5);
        dag = new GenericTree<>(true, 5);
    }

    /**
     * Tests edge addition in an undirected tree and cycle prevention.
     */
    @Test
    public void testEdgeAdditionUndirectedCyclePrevention() {
        assertTrue(undirectedTree.addEdge(1, 2));
        assertTrue(undirectedTree.addEdge(2, 3));
        assertTrue(undirectedTree.addEdge(3, 4));
        assertTrue(undirectedTree.addEdge(4, 5));
        assertFalse(undirectedTree.addEdge(5, 1)); // should create cycle

        assertEquals(4, undirectedTree.getDepth(5));
        assertEquals(5, undirectedTree.getSubtreeSize(1));
        assertEquals(3, undirectedTree.getSubtreeSize(3));
    }

    /**
     * Tests edge removal and recomputation of depth.
     */
    @Test
    public void testRemoveEdgeUndirected() {
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(2, 3);

        assertEquals(2, undirectedTree.getDepth(3));

        assertTrue(undirectedTree.removeEdge(2, 3));
        assertEquals(-1, undirectedTree.getDepth(3)); // disconnected now
    }

    /**
     * Tests BFS traversal correctness.
     */
    @Test
    public void testBFSOrder() {
        List<Integer> visited = new ArrayList<>();
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(1, 3);
        undirectedTree.addEdge(2, 4);
        undirectedTree.addEdge(3, 5);

        undirectedTree.bfs(1, visited::add);
        assertEquals(5, visited.size());
        assertTrue(visited.containsAll(List.of(1, 2, 3, 4, 5)));
    }

    /**
     * Tests DFS traversal correctness.
     */
    @Test
    public void testDFSOrder() {
        List<Integer> visited = new ArrayList<>();
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(2, 3);
        undirectedTree.addEdge(3, 4);
        undirectedTree.dfs(1, visited::add);

        assertEquals(List.of(1, 2, 3, 4), visited);
    }

    /**
     * Tests LCA queries after automatic DFS and lifting computation.
     */
    @Test
    public void testBinaryLiftingAndLCA() {
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(1, 3);
        undirectedTree.addEdge(2, 4);
        undirectedTree.addEdge(2, 5);

        assertEquals(2, undirectedTree.getLCA(4, 5));
        assertEquals(1, undirectedTree.getLCA(4, 3));
        assertEquals(1, undirectedTree.getLCA(1, 5));
        assertNull(undirectedTree.getLCA(99, 1)); // node not in tree
    }

    /**
     * Tests default behavior for non-existent nodes.
     */
    @Test
    public void testDepthAndSubtreeDefaults() {
        assertEquals(-1, undirectedTree.getDepth(999));
        assertEquals(0, undirectedTree.getSubtreeSize(999));
    }

    /**
     * Tests behavior when null edges are added.
     */
    @Test
    public void testEdgeAdditionNullSafety() {
        assertThrows(NullPointerException.class, () -> undirectedTree.addEdge(null, 2));
        assertThrows(NullPointerException.class, () -> dag.addEdge(1, null));
    }

    /**
     * Tests edge removal for nonexistent or duplicate removals.
     */
    @Test
    public void testRemoveEdgeMissingEdge() {
        // Edge not present
        assertFalse(undirectedTree.removeEdge(1, 2));

        // Build 1 - 2 - 3 - 4
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(2, 3);
        undirectedTree.addEdge(3, 4);

        // Edge in middle (not leaf) — must not allow removal
        assertFalse(undirectedTree.removeEdge(2, 3));

        // Remove edge from leaf (1 is leaf)
        assertTrue(undirectedTree.removeEdge(1, 2));

        // Now root must have shifted from 1 → 2
        // Check recomputation: subtree size from new root (2) should include 3 and 4
        assertEquals(3, undirectedTree.getSubtreeSize(2));
        assertEquals(0, undirectedTree.getDepth(2));
        assertEquals(0, undirectedTree.getSubtreeSize(1)); // disconnected node
        assertEquals(-1, undirectedTree.getDepth(1)); // no longer reachable

        // Already removed
        assertFalse(undirectedTree.removeEdge(1, 2));
    }


    /**
     * Tests that cycles are detected and prevented in DAGs.
     */
    @Test
    public void testDirectedEdgeCyclePrevention() {
        assertTrue(dag.addEdge(1, 2));
        assertTrue(dag.addEdge(2, 3));
        assertTrue(dag.addEdge(3, 4));
        assertFalse(dag.addEdge(4, 1)); // would form a cycle

        assertTrue(dag.addEdge(4, 5)); // allowed
    }

    /**
     * Tests correctness of topological sorting.
     */
    @Test
    public void testTopologicalSortCorrectness() {
        dag.addEdge(5, 2);
        dag.addEdge(5, 0);
        dag.addEdge(4, 0);
        dag.addEdge(4, 1);
        dag.addEdge(2, 3);
        dag.addEdge(3, 1);

        List<Integer> topo = dag.getTopologicalOrder();
        assertEquals(6, topo.size());

        // Check ordering constraints
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < topo.size(); i++) pos.put(topo.get(i), i);

        assertTrue(pos.get(5) < pos.get(2));
        assertTrue(pos.get(2) < pos.get(3));
        assertTrue(pos.get(3) < pos.get(1));
        assertTrue(pos.get(4) < pos.get(0));
    }

    /**
     * Ensures topological sort fails on undirected tree.
     */
    @Test
    public void testTopologicalSortUndirectedThrows() {
        undirectedTree.addEdge(1, 2);
        undirectedTree.addEdge(2, 3);

        assertThrows(UnsupportedOperationException.class, () -> undirectedTree.getTopologicalOrder());
    }
}
