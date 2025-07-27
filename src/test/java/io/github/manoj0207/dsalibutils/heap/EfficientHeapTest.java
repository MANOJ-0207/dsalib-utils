package io.github.manoj0207.dsalibutils.heap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EfficientHeap}.
 * Covers functionality for min-heap, max-heap, value removal, update, and exception handling.
 */
class EfficientHeapTest {

    private EfficientHeap<Integer> minHeap;
    private EfficientHeap<Integer> maxHeap;

    @BeforeEach
    void setUp() {
        minHeap = new EfficientHeap<>(true);  // Natural order (min-heap)
        maxHeap = new EfficientHeap<>(false); // Reverse order (max-heap)
    }

    @Test
    void testAddAndPeek_minHeap() {
        minHeap.add(5);
        minHeap.add(3);
        minHeap.add(7);
        assertEquals(3, minHeap.peek()); // Smallest should be at root
    }

    @Test
    void testAddAndPeek_maxHeap() {
        maxHeap.add(5);
        maxHeap.add(3);
        maxHeap.add(7);
        assertEquals(7, maxHeap.peek()); // Largest should be at root
    }

    @Test
    void testPoll_minHeap() {
        minHeap.add(4);
        minHeap.add(1);
        minHeap.add(6);
        assertEquals(1, minHeap.poll()); // Should remove and return smallest
        assertEquals(4, minHeap.peek());
    }

    @Test
    void testPoll_maxHeap() {
        maxHeap.add(4);
        maxHeap.add(1);
        maxHeap.add(6);
        assertEquals(6, maxHeap.poll()); // Should remove and return largest
        assertEquals(4, maxHeap.peek());
    }

    @Test
    void testRemove_existingElement() {
        minHeap.add(10);
        minHeap.add(5);
        minHeap.add(3);
        assertTrue(minHeap.remove(5));
        assertFalse(minHeap.remove(99)); // non-existing
        assertEquals(3, minHeap.peek());
    }

    @Test
    void testUpdate_existingElement() {
        minHeap.add(9);
        minHeap.add(6);
        minHeap.add(4);
        assertTrue(minHeap.update(6, 2)); // 4 replaced with 2
        assertEquals(2, minHeap.peek());
    }

    @Test
    void testUpdate_nonExistingElement() {
        minHeap.add(5);
        assertFalse(minHeap.update(99, 1)); // no 99 in heap
    }

    @Test
    void testIsEmpty() {
        assertTrue(minHeap.isEmpty());
        minHeap.add(1);
        assertFalse(minHeap.isEmpty());
        minHeap.poll();
        assertTrue(minHeap.isEmpty());
    }

    @Test
    void testPeekEmpty_throwsException() {
        assertThrows(NoSuchElementException.class, () -> minHeap.peek());
    }

    @Test
    void testPollEmpty_throwsException() {
        assertThrows(NoSuchElementException.class, () -> minHeap.poll());
    }

    @Test
    void testCustomComparator() {
        // Absolute value comparator
        EfficientHeap<Integer> absHeap = new EfficientHeap<Integer>(Comparator.comparingInt(Math::abs));
        absHeap.add(-10);
        absHeap.add(5);
        absHeap.add(-3);
        assertEquals(-3, absHeap.peek()); // Smallest absolute value
    }

    @Test
    void testMultipleRemovals_sameValueInstances() {
        minHeap.add(5);
        minHeap.add(5);
        minHeap.add(5);
        assertTrue(minHeap.remove(5));
        assertTrue(minHeap.remove(5));
        assertTrue(minHeap.remove(5));
        assertFalse(minHeap.remove(5)); // All removed now
    }
}
