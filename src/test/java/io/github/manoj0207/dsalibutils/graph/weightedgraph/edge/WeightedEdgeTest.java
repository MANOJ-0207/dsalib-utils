package io.github.manoj0207.dsalibutils.graph.weightedgraph.edge;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WeightedEdgeTest {

    @Test
    void testFieldValues() {
        WeightedEdge<String> edge = new WeightedEdge<>("A", 10);
        assertEquals("A", edge.node());
        assertEquals(10, edge.weight());
    }

    @Test
    void testEqualityIgnoresWeight() {
        WeightedEdge<String> e1 = new WeightedEdge<>("B", 5);
        WeightedEdge<String> e2 = new WeightedEdge<>("B", 10);
        WeightedEdge<String> e3 = new WeightedEdge<>("C", 5);

        assertEquals(e1, e2);  // same node, different weight
        assertNotEquals(e1, e3);
    }

    @Test
    void testHashCodeConsistencyWithEquals() {
        WeightedEdge<String> e1 = new WeightedEdge<>("X", 5);
        WeightedEdge<String> e2 = new WeightedEdge<>("X", 100);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testCompareToByWeight() {
        WeightedEdge<String> e1 = new WeightedEdge<>("A", 3);
        WeightedEdge<String> e2 = new WeightedEdge<>("A", 7);
        assertTrue(e1.compareTo(e2) < 0);
        assertTrue(e2.compareTo(e1) > 0);
        assertEquals(0, e1.compareTo(new WeightedEdge<>("A", 3)));
    }

    @Test
    void testToStringFormat() {
        WeightedEdge<String> edge = new WeightedEdge<>("Z", 42);
        assertEquals("Z (wt: 42)", edge.toString());
    }

    @Test
    void testPriorityQueueOrdering() {
        PriorityQueue<WeightedEdge<String>> pq = new PriorityQueue<>();
        pq.add(new WeightedEdge<>("A", 5));
        pq.add(new WeightedEdge<>("B", 2));
        pq.add(new WeightedEdge<>("C", 10));

        assertEquals("B", pq.poll().node());
        assertEquals("A", pq.poll().node());
        assertEquals("C", pq.poll().node());
    }

    @Test
    void testUsedInHashSet() {
        Set<WeightedEdge<String>> set = new HashSet<>();
        set.add(new WeightedEdge<>("M", 1));
        set.add(new WeightedEdge<>("M", 99));

        assertEquals(1, set.size());
    }

}
