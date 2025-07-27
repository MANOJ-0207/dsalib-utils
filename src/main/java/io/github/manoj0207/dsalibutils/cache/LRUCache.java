package io.github.manoj0207.dsalibutils.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) Cache implementation.
 *
 * <p>Evicts the least recently accessed element when capacity is reached.
 * This class uses a combination of a HashMap and a Doubly Linked List to
 * ensure O(1) time complexity for both get and put operations.</p>
 *
 * @param <K> the type of keys used in the cache
 * @param <V> the type of values stored in the cache
 */
public class LRUCache<K, V> extends AbstractCache<K, V> {

    /** Maps keys to their corresponding linked list nodes */
    private final Map<K, Node<K, V>> nodeMap;

    /** Doubly linked list to maintain LRU order */
    private final DoublyLinkedList<K, V> dll;

    /**
     * Constructs an LRU cache with the specified capacity.
     *
     * @param capacity the maximum number of entries
     * @throws IllegalArgumentException if capacity is negative
     */
    public LRUCache(int capacity) {
        super(capacity);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity cannot be negative.");
        }
        this.nodeMap = new HashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    /**
     * Retrieves the value associated with the key, if it exists.
     * Moves the entry to the front (most recently used).
     *
     * @param key the key to access
     * @return the value if present, or null otherwise
     * @throws IllegalArgumentException if key is null
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (!nodeMap.containsKey(key)) return null;

        Node<K, V> node = nodeMap.get(key);
        dll.moveToFront(node);
        return node.value;
    }

    /**
     * Inserts or updates a key-value pair in the cache.
     * If the key exists, updates its value and marks it as most recently used.
     * If not, adds a new entry and evicts the least recently used one if necessary.
     *
     * @param key   the key to insert/update
     * @param value the value to associate with the key
     * @throws IllegalArgumentException if key is null
     */
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }

        if (nodeMap.containsKey(key)) {
            Node<K, V> node = nodeMap.get(key);
            node.value = value;
            dll.moveToFront(node);
        } else {
            if (nodeMap.size() >= capacity) {
                Node<K, V> lru = dll.removeTail();
                if (lru != null) {
                    nodeMap.remove(lru.key);
                }
            }
            Node<K, V> newNode = new Node<>(key, value);
            dll.addFront(newNode);
            nodeMap.put(key, newNode);
        }
    }

    /**
     * Internal doubly linked list node to hold key-value pairs.
     */
    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;

        Node(K k, V v) {
            this.key = k;
            this.value = v;
        }
    }

    /**
     * Internal doubly linked list for tracking LRU order.
     */
    static class DoublyLinkedList<K, V> {
        Node<K, V> head, tail;

        /**
         * Adds a node to the front of the list (most recently used).
         */
        void addFront(Node<K, V> node) {
            node.next = head;
            node.prev = null;
            if (head != null) {
                head.prev = node;
            }
            head = node;
            if (tail == null) {
                tail = head;
            }
        }

        /**
         * Moves the given node to the front of the list.
         */
        void moveToFront(Node<K, V> node) {
            if (node == head) return;

            // Remove node from current position
            if (node.prev != null) {
                node.prev.next = node.next;
            }
            if (node.next != null) {
                node.next.prev = node.prev;
            }

            if (node == tail) {
                tail = node.prev;
            }

            // Move to front
            node.prev = null;
            node.next = head;
            if (head != null) {
                head.prev = node;
            }
            head = node;
        }

        /**
         * Removes and returns the least recently used node (tail).
         *
         * @return the removed node, or null if the list is empty
         */
        Node<K, V> removeTail() {
            if (tail == null) return null;

            Node<K, V> removed = tail;
            tail = tail.prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
            return removed;
        }
    }
}
