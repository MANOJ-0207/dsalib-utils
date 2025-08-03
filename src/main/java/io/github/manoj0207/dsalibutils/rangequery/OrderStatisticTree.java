package io.github.manoj0207.dsalibutils.rangequery;

import java.util.*;

/**
 * A self-balancing multiset tree supporting order statistics using an AVL tree.
 *
 * <p>This data structure allows insertion and deletion of duplicate elements, and provides
 * efficient order-statistics operations like finding the k-th smallest element or counting
 * elements strictly less than a given key.</p>
 *
 * <p><b>Time Complexity:</b> O(log n) per operation due to AVL balancing.</p>
 *
 * @param <T> the comparable type of elements stored in the tree
 */
public class OrderStatisticTree<T extends Comparable<T>> {

    private class Node {
        T val;
        int count, size, height;
        Node left, right;

        Node(T val) {
            this.val = val;
            this.count = 1;
            this.size = 1;
            this.height = 1;
        }
    }

    private Node root;



    /**
     * Constructs an empty OrderStatisticTree.
     *
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public OrderStatisticTree() {
    }
    /**
     * Constructs the tree and inserts all elements from the provided list.
     *
     * @param values list of elements to insert (duplicates allowed)
     * <p><b>Time Complexity:</b> O(n log n)</p>
     */
    public OrderStatisticTree(List<T> values) {
        if (values == null)
            throw new IllegalArgumentException("Input list cannot be null");
        for (T val : values) {
            insert(val);
        }
    }

    /**
     * Constructs the tree and inserts all elements from the provided set.
     *
     * @param values set of elements to insert (duplicates not allowed in set)
     * <p><b>Time Complexity:</b> O(n log n)</p>
     */
    public OrderStatisticTree(Set<T> values) {
        if (values == null)
            throw new IllegalArgumentException("Input set cannot be null");
        for (T val : values) {
            insert(val);
        }
    }

    /**
     * Inserts an element into the multiset.
     *
     * <p>If the element already exists, its count is incremented.</p>
     *
     * @param val the element to insert
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public void insert(T val) {
        root = insert(root, val);
    }

    /**
     * Removes one occurrence of an element from the multiset.
     *
     * <p>If the element occurs multiple times, only one copy is removed. If it's the last copy,
     * the node is deleted from the tree.</p>
     *
     * @param val the element to remove
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public void erase(T val) {
        root = erase(root, val);
    }

    /**
     * Returns the number of times the given element appears in the multiset.
     *
     * @param val the element to count
     * @return the count of the element in the multiset (0 if not present)
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public int count(T val) {
        Node node = root;
        while (node != null) {
            int cmp = val.compareTo(node.val);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return node.count;
        }
        return 0;
    }

    /**
     * Returns the number of elements strictly less than the given value.
     *
     * @param val the upper bound value
     * @return number of elements strictly less than {@code val}
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public int orderOfKey(T val) {
        return orderOfKey(root, val);
    }

    /**
     * Finds the k-th smallest element in the multiset (0-based index).
     *
     * @param k the index (0-based)
     * @return the element at index {@code k} if exists, otherwise {@code null}
     * <p><b>Time Complexity:</b> O(log n)</p>
     */
    public T findByOrder(int k) {
        if (k < 0 || k >= size()) return null;
        return findByOrder(root, k);
    }

    /**
     * Returns the total number of elements in the multiset (including duplicates).
     *
     * @return the total number of elements
     * <p><b>Time Complexity:</b> O(1)</p>
     */
    public int size() {
        return getSize(root);
    }

    /**
     * Checks whether the multiset contains at least one occurrence of the specified value.
     *
     * @param val the value to check for presence
     * @return <code>true</code> if the value exists in the tree (i.e., count > 0), <code>false</code> otherwise
     *
     * <p><b>Time Complexity:</b> <p>O(log n)</p>
     *
     * if at least one copy of the value exists, regardless of how many duplicates are present.</p>
     */
    public boolean contains(T val) {
        return count(val) > 0;
    }

    // --- Internal methods ---

    private Node insert(Node node, T val) {
        if (node == null) return new Node(val);
        int cmp = val.compareTo(node.val);
        if (cmp < 0) node.left = insert(node.left, val);
        else if (cmp > 0) node.right = insert(node.right, val);
        else node.count++;
        return balance(update(node));
    }

    private Node erase(Node node, T val) {
        if (node == null) return null;
        int cmp = val.compareTo(node.val);
        if (cmp < 0) node.left = erase(node.left, val);
        else if (cmp > 0) node.right = erase(node.right, val);
        else {
            if (node.count > 1) {
                node.count--;
                return update(node);
            }
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = minValueNode(node.right);
            node.val = successor.val;
            node.count = successor.count;
            successor.count = 1;
            node.right = erase(node.right, successor.val);
        }
        return balance(update(node));
    }

    private Node minValueNode(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int orderOfKey(Node node, T val) {
        if (node == null) return 0;
        int cmp = val.compareTo(node.val);
        if (cmp <= 0) return orderOfKey(node.left, val);
        return getSize(node.left) + node.count + orderOfKey(node.right, val);
    }

    private T findByOrder(Node node, int k) {
        int leftSize = getSize(node.left);
        if (k < leftSize) return findByOrder(node.left, k);
        else if (k < leftSize + node.count) return node.val;
        return findByOrder(node.right, k - leftSize - node.count);
    }
    // --- AVL Tree utilities ---

    private Node update(Node node) {
        node.size = node.count + getSize(node.left) + getSize(node.right);
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        return node;
    }



    private int getSize(Node node) {
        return node == null ? 0 : node.size;
    }

    private int getHeight(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return getHeight(node.left) - getHeight(node.right);
    }

    private Node balance(Node node) {
        int balance = getBalance(node);

        if (balance > 1) {
            if (getBalance(node.left) < 0) node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1) {
            if (getBalance(node.right) > 0) node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        update(y);
        update(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        update(x);
        update(y);
        return y;
    }
}
