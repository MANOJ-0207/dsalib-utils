package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testEqualitySameOrder() {
        Edge<String> e1 = new Edge<>("A", "B");
        Edge<String> e2 = new Edge<>("A", "B");
        assertEquals(e1, e2);
    }

    @Test
    void testEqualityReversedOrder() {
        Edge<String> e1 = new Edge<>("A", "B");
        Edge<String> e2 = new Edge<>("B", "A");
        assertEquals(e1, e2);
    }

    @Test
    void testInequalityDifferentNodes() {
        Edge<String> e1 = new Edge<>("A", "B");
        Edge<String> e2 = new Edge<>("A", "C");
        assertNotEquals(e1, e2);
    }

    @Test
    void testHashCodeUndirectedConsistency() {
        Edge<String> e1 = new Edge<>("A", "B");
        Edge<String> e2 = new Edge<>("B", "A");
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testToStringFormat() {
        Edge<String> edge = new Edge<>("A", "B");
        assertEquals("(A — B)", edge.toString());
    }

    @Test
    void testEdgeInSetUndirected() {
        Set<Edge<String>> set = new HashSet<>();
        set.add(new Edge<>("X", "Y"));
        assertTrue(set.contains(new Edge<>("Y", "X")));
    }

    @Test
    void testSelfLoopEdgeEquality() {
        Edge<String> edge = new Edge<>("Z", "Z");
        assertEquals(edge, new Edge<>("Z", "Z"));
        assertEquals(edge.hashCode(), new Edge<>("Z", "Z").hashCode());
    }
}
