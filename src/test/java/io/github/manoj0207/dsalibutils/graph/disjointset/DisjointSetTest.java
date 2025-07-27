package io.github.manoj0207.dsalibutils.graph.disjointset;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DisjointSet}.
 */
class DisjointSetTest {

    @Test
    void testMakeSetAndFind() {
        DisjointSet<String> ds = new DisjointSet<>();
        ds.makeSet("A");
        ds.makeSet("B");

        assertEquals("A", ds.find("A"));
        assertEquals("B", ds.find("B"));
    }

    @Test
    void testUnionAndConnected() {
        DisjointSet<String> ds = new DisjointSet<>();

        ds.makeSet("A");
        ds.makeSet("B");
        ds.makeSet("C");

        assertTrue(ds.union("A", "B"));
        assertTrue(ds.isConnected("A", "B"));

        assertFalse(ds.isConnected("A", "C"));

        assertTrue(ds.union("B", "C"));
        assertTrue(ds.isConnected("A", "C"));
    }

    @Test
    void testUnionReturnsFalseIfAlreadyConnected() {
        DisjointSet<Integer> ds = new DisjointSet<>(List.of(1, 2, 3));
        ds.union(1, 2);
        assertFalse(ds.union(1, 2)); // already connected
    }

    @Test
    void testFindWithPathCompression() {
        DisjointSet<Integer> ds = new DisjointSet<>();
        for (int i = 1; i <= 5; i++) {
            ds.makeSet(i);
        }

        // Build: 1 <- 2 <- 3 <- 4 <- 5
        ds.union(1, 2);
        ds.union(2, 3);
        ds.union(3, 4);
        ds.union(4, 5);

        Integer root = ds.find(5);
        for (int i = 1; i <= 5; i++) {
            assertEquals(root, ds.find(i));
        }

        Map<Integer, Integer> parentMap = ds.getParentMap();
        // After compression, all should point directly or closely to root
        assertEquals(root, parentMap.get(5));
    }

    @Test
    void testLazyInitializationInFind() {
        DisjointSet<String> ds = new DisjointSet<>();
        assertEquals("X", ds.find("X")); // lazy initialized
        assertTrue(ds.isConnected("X", "X"));
    }

    @Test
    void testConstructorWithIterable() {
        List<String> elements = List.of("P", "Q", "R");
        DisjointSet<String> ds = new DisjointSet<>(elements);

        for (String el : elements) {
            assertEquals(el, ds.find(el));
        }

        assertFalse(ds.isConnected("P", "Q"));
    }

    @Test
    void testGetParentMap() {
        DisjointSet<Integer> ds = new DisjointSet<>(List.of(1, 2, 3));
        ds.union(1, 2);

        Map<Integer, Integer> parentSnapshot = ds.getParentMap();

        assertTrue(parentSnapshot.containsKey(1));
        assertTrue(parentSnapshot.containsKey(2));
        assertEquals(ds.find(1), parentSnapshot.get(2));
    }

    @Test
    void testMultipleUnionsAndRankOptimization() {
        DisjointSet<String> ds = new DisjointSet<>(List.of("A", "B", "C", "D"));

        ds.union("A", "B");
        ds.union("C", "D");
        ds.union("A", "C");

        assertTrue(ds.isConnected("A", "D"));
        assertTrue(ds.isConnected("B", "C"));
    }
}
