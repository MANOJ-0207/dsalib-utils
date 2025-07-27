package io.github.manoj0207.dsalibutils.rangequery;

import java.util.*;

/**
 * A multiset data structure supporting order statistics using a binary search tree.
 *
 * <p>Features:
 * <ul>
 *   <li>Insertion and removal of elements (with duplicates)</li>
 *   <li>Counting occurrences of a value</li>
 *   <li>Querying the number of elements strictly less than a value ({@code orderOfKey})</li>
 *   <li>Finding the element by k-th index ({@code findByOrder})</li>
 * </ul>
 *
 * <p>Analogous to C++ PBDS tree with {@code less_equal<T>} comparator.
 *
 * @param <T> the element type (must implement {@link Comparable})
 */
public class OrderStatisticTree<T extends Comparable<T>> {
    /**
     * Represents a node in the binary search tree.
     */
    private class Node {
        T val;
        int count; // frequency of val
        int size;  // total size of subtree rooted at this node (including duplicates)
        Node left, right;

        Node(T val) {
            this.val = val;
            this.count = 1;
            this.size = 1;
        }
    }

    private Node root;

    /**
     * Inserts an element into the tree.
     *
     * @param val the value to insert
     */
    public void insert(T val) {
        root = insert(root, val);
    }

    private Node insert(Node node, T val) {
        if (node == null) return new Node(val);

        int cmp = val.compareTo(node.val);
        if (cmp < 0) {
            node.left = insert(node.left, val);
        } else if (cmp > 0) {
            node.right = insert(node.right, val);
        } else {
            node.count++;
        }
        updateSize(node);
        return node; // no balancing (can add AVL/Treap/RBTree here)
    }

    /**
     * Removes one occurrence of the value from the tree (if present).
     *
     * @param val the value to remove
     */
    public void erase(T val) {
        root = erase(root, val);
    }

    private Node erase(Node node, T val) {
        if (node == null) return null;

        int cmp = val.compareTo(node.val);
        if (cmp < 0) {
            node.left = erase(node.left, val);
        } else if (cmp > 0) {
            node.right = erase(node.right, val);
        } else {
            if (node.count > 1) {
                node.count--;
            } else {
                if (node.left == null) return node.right;
                if (node.right == null) return node.left;

                Node successor = minValueNode(node.right);
                node.val = successor.val;
                node.count = successor.count;
                successor.count = 1;
                node.right = erase(node.right, successor.val);
            }
        }
        updateSize(node);
        return node;
    }

    private Node minValueNode(Node node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    /**
     * Returns the number of times a value appears in the tree.
     *
     * @param val the value to check
     * @return the frequency of the value
     */
    public int count(T val) {
        Node node = root;
        while (node != null) {
            int cmp = val.compareTo(node.val);
            if (cmp < 0)
                node = node.left;
            else if (cmp > 0)
                node = node.right;
            else
                return node.count;
        }
        return 0;
    }

    /**
     * Returns the number of elements strictly less than {@code val}.
     *
     * @param val the value to compare
     * @return count of elements < {@code val}
     */
    public int orderOfKey(T val) {
        return orderOfKey(root, val);
    }

    private int orderOfKey(Node node, T val) {
        if (node == null) return 0;

        int cmp = val.compareTo(node.val);
        if (cmp <= 0) {
            return orderOfKey(node.left, val);
        } else {
            return size(node.left) + node.count + orderOfKey(node.right, val);
        }
    }

    /**
     * Returns the k-th smallest element in the tree (0-based index).
     *
     * @param k the 0-based index (0 = smallest)
     * @return the k-th smallest element, or {@code null} if out of bounds
     */
    public T findByOrder(int k) {
        if (k < 0 || k >= size()) return null;
        return findByOrder(root, k);
    }

    private T findByOrder(Node node, int k) {
        if (node == null) return null;

        int leftSize = size(node.left);

        if (k < leftSize) {
            return findByOrder(node.left, k);
        } else if (k < leftSize + node.count) {
            return node.val;
        } else {
            return findByOrder(node.right, k - leftSize - node.count);
        }
    }

    /**
     * Returns the total number of elements in the tree, including duplicates.
     *
     * @return total number of elements
     */
    public int size() {
        return size(root);
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private void updateSize(Node node) {
        if (node != null) {
            node.size = node.count + size(node.left) + size(node.right);
        }
    }
}
