package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DetailedEdgeTest {

    @Test
    void testFieldValues() {
        DetailedEdge<String> edge = new DetailedEdge<>("A", "B", 5);
        assertEquals("A", edge.source());
        assertEquals("B", edge.dest());
        assertEquals(5, edge.weight());
    }

    @Test
    void testEqualsAndHashCode() {
        DetailedEdge<String> e1 = new DetailedEdge<>("A", "B", 10);
        DetailedEdge<String> e2 = new DetailedEdge<>("A", "B", 10);
        DetailedEdge<String> e3 = new DetailedEdge<>("B", "A", 10); // different direction
        DetailedEdge<String> e4 = new DetailedEdge<>("A", "B", 20); // different weight

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());

        assertNotEquals(e1, e3);
        assertNotEquals(e1, e4);
    }

    @Test
    void testToString() {
        DetailedEdge<String> edge = new DetailedEdge<>("X", "Y", 42);
        assertEquals("X --42→ Y", edge.toString());
    }

    @Test
    void testEdgeInSet() {
        Set<DetailedEdge<String>> edgeSet = new HashSet<>();
        edgeSet.add(new DetailedEdge<>("M", "N", 8));
        assertTrue(edgeSet.contains(new DetailedEdge<>("M", "N", 8)));
        assertFalse(edgeSet.contains(new DetailedEdge<>("N", "M", 8)));
    }

    @Test
    void testDifferentGenericTypes() {
        DetailedEdge<Integer> edge = new DetailedEdge<>(1, 2, 99);
        assertEquals(1, edge.source());
        assertEquals(2, edge.dest());
        assertEquals(99, edge.weight());
    }
}
