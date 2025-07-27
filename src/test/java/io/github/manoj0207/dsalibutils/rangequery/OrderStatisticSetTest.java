package io.github.manoj0207.dsalibutils.rangequery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStatisticSetTest {

    private OrderStatisticSet<String> stringSet;
    private OrderStatisticSet<Integer> intSet;

    @BeforeEach
    void setUp() {
        stringSet = new OrderStatisticSet<>(Set.of("apple", "banana", "cherry", "date", "fig"));
        intSet = new OrderStatisticSet<>(Set.of(10, 20, 30, 40, 50));
    }

    @Test
    void testInsertAndCountStrings() {
        stringSet.insert("banana");
        stringSet.insert("banana");
        stringSet.insert("apple");

        assertEquals(2, stringSet.count("banana"));
        assertEquals(1, stringSet.count("apple"));
        assertEquals(0, stringSet.count("grape")); // not in universe
    }

    @Test
    void testCountLessThanStrings() {
        stringSet.insert("apple");
        stringSet.insert("banana");
        stringSet.insert("cherry");

        assertEquals(0, stringSet.countLessThan("apple"));
        assertEquals(1, stringSet.countLessThan("banana"));
        assertEquals(2, stringSet.countLessThan("cherry"));
        assertEquals(3, stringSet.countLessThan("date"));
    }

    @Test
    void testKthSmallestStrings() {
        stringSet.insert("banana");
        stringSet.insert("fig");
        stringSet.insert("banana");
        stringSet.insert("apple");

        assertEquals("apple", stringSet.kthSmallest(1));
        assertEquals("banana", stringSet.kthSmallest(2));
        assertEquals("banana", stringSet.kthSmallest(3));
        assertEquals("fig", stringSet.kthSmallest(4));
        assertNull(stringSet.kthSmallest(0));
        assertNull(stringSet.kthSmallest(5));
    }

    @Test
    void testInsertOutsideUniverse() {
        stringSet.insert("grape"); // not in universe
        assertEquals(0, stringSet.count("grape"));
        assertEquals(0, stringSet.size());
    }

    @Test
    void testRemoveAndEmptyBehavior() {
        stringSet.insert("apple");
        stringSet.insert("apple");
        stringSet.remove("apple");
        assertTrue(stringSet.contains("apple"));
        stringSet.remove("apple");
        assertFalse(stringSet.contains("apple"));
        stringSet.remove("apple"); // underflow, should do nothing
        assertEquals(0, stringSet.count("apple"));
    }

    @Test
    void testContainsAndSizeGeneric() {
        assertEquals(0, stringSet.size());
        stringSet.insert("cherry");
        stringSet.insert("banana");
        stringSet.insert("banana");

        assertTrue(stringSet.contains("banana"));
        assertFalse(stringSet.contains("apple"));
        assertEquals(3, stringSet.size());
    }

    @Test
    void testConstructorWithList() {
        List<String> values = List.of("apple", "banana", "banana", "fig");
        OrderStatisticSet<String> setFromList = new OrderStatisticSet<>(values);

        assertEquals(4, setFromList.size());
        assertEquals(2, setFromList.count("banana"));
        assertEquals("apple", setFromList.kthSmallest(1));
        assertEquals("banana", setFromList.kthSmallest(2));
    }

    @Test
    void testExceptionOnEmptyOrNullUniverse() {
        assertThrows(IllegalArgumentException.class, () -> new OrderStatisticSet<String>(Set.of()));
        assertThrows(IllegalArgumentException.class, () -> new OrderStatisticSet<String>((Set<String>) null));
        assertThrows(IllegalArgumentException.class, () -> new OrderStatisticSet<String>((List<String>) null));
        assertThrows(IllegalArgumentException.class, () -> new OrderStatisticSet<Integer>(List.<Integer>of()));
    }

    @Test
    void testMixedOperationsIntSet() {
        intSet.insert(10);
        intSet.insert(10);
        intSet.insert(30);
        intSet.insert(50);

        assertEquals(10, intSet.kthSmallest(1));
        assertEquals(10, intSet.kthSmallest(2));
        assertEquals(30, intSet.kthSmallest(3));
        assertEquals(50, intSet.kthSmallest(4));

        intSet.remove(10);
        assertEquals(10, intSet.kthSmallest(1));
        intSet.remove(10);
        assertEquals(30, intSet.kthSmallest(1));
        intSet.remove(30);
        intSet.remove(50);
        assertNull(intSet.kthSmallest(1));
    }

    @Test
    void testBoundaryKthSmallest() {
        intSet.insert(10);
        intSet.insert(20);
        intSet.insert(30);
        assertNull(intSet.kthSmallest(0));
        assertNull(intSet.kthSmallest(-1));
        assertNull(intSet.kthSmallest(10));
    }

    @Test
    void testCountLessThanNonExisting() {
        intSet.insert(10);
        intSet.insert(20);
        assertEquals(0, intSet.countLessThan(5));
        assertEquals(2, intSet.countLessThan(30));
        assertEquals(1, intSet.countLessThan(17));
    }
}
