package io.github.manoj0207.dsalibutils.graph.unweightedgraph;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testUndirectedEqualityAndHashCode() {
        Edge<String> ab = new Edge<>("A", "B", false);
        Edge<String> ba = new Edge<>("B", "A", false);
        Edge<String> ac = new Edge<>("A", "C", false);

        assertEquals(ab, ba); // same edge, different order
        assertEquals(ab.hashCode(), ba.hashCode());
        assertNotEquals(ab, ac); // different node
    }

    @Test
    void testDirectedEqualityAndHashCode() {
        Edge<String> ab = new Edge<>("A", "B", true);
        Edge<String> ab2 = new Edge<>("A", "B", true);
        Edge<String> ba = new Edge<>("B", "A", true);

        assertEquals(ab, ab2); // same edge and direction
        assertNotEquals(ab, ba); // reversed direction
        assertNotEquals(ab.hashCode(), ba.hashCode());
    }

    @Test
    void testDirectedAndUndirectedNotEqual() {
        Edge<String> directed = new Edge<>("A", "B", true);
        Edge<String> undirected = new Edge<>("A", "B", false);

        assertNotEquals(directed, undirected);
    }

    @Test
    void testToStringFormat() {
        Edge<String> undirected = new Edge<>("A", "B", false);
        Edge<String> directed = new Edge<>("A", "B", true);

        assertEquals("(A — B)", undirected.toString());
        assertEquals("(A → B)", directed.toString());
    }

    @Test
    void testUndirectedSetContainment() {
        Set<Edge<String>> undirectedSet = new HashSet<>();
        undirectedSet.add(new Edge<>("X", "Y", false));
        assertTrue(undirectedSet.contains(new Edge<>("Y", "X", false)));
    }

    @Test
    void testDirectedSetContainment() {
        Set<Edge<String>> directedSet = new HashSet<>();
        directedSet.add(new Edge<>("X", "Y", true));
        assertTrue(directedSet.contains(new Edge<>("X", "Y", true)));
        assertFalse(directedSet.contains(new Edge<>("Y", "X", true)));
    }

    @Test
    void testSelfLoopEqualityAndHashCode() {
        Edge<String> loopUndirected1 = new Edge<>("Z", "Z", false);
        Edge<String> loopUndirected2 = new Edge<>("Z", "Z", false);
        assertEquals(loopUndirected1, loopUndirected2);
        assertEquals(loopUndirected1.hashCode(), loopUndirected2.hashCode());

        Edge<String> loopDirected1 = new Edge<>("Z", "Z", true);
        Edge<String> loopDirected2 = new Edge<>("Z", "Z", true);
        assertEquals(loopDirected1, loopDirected2);
        assertEquals(loopDirected1.hashCode(), loopDirected2.hashCode());
    }
}
