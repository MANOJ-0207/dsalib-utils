package io.github.manoj0207.dsalibutils.stringutils;

import java.util.*;

/**
 * A Trie (prefix tree) implementation for string storage and efficient querying.
 * Supports insertion, deletion, exact search, prefix search, and frequency counts.
 */
public class Trie {
    private final TrieNode root;

    /**
     * Constructs an empty Trie.
     */
    public Trie() {
        this.root = new TrieNode();
    }

    /**
     * Inserts a word into the Trie. Allows duplicate insertions and tracks frequency.
     *
     * @param word the word to insert (must not be null)
     * @throws IllegalArgumentException if the word is null
     */
    public void insert(String word) {
        if (word == null) throw new IllegalArgumentException("Word cannot be null");

        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, c -> new TrieNode());
            node.prefixCount++;
        }
        node.isEndOfWord = true;
        node.wordCount++;
    }

    /**
     * Searches for an exact word in the Trie.
     *
     * @param word the word to search
     * @return true if found, false otherwise
     */
    public boolean search(String word) {
        TrieNode node = getNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Checks if any word in the Trie starts with the given prefix.
     *
     * @param prefix the prefix to match
     * @return true if found, false otherwise
     */
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    /**
     * Deletes one instance of a word from the Trie.
     * If inserted multiple times, only one instance is removed.
     *
     * @param word the word to delete
     */
    public void delete(String word) {
        if (word == null || !search(word)) return;

        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            TrieNode next = node.children.get(ch);
            next.prefixCount--;
            if (next.prefixCount == 0) {
                node.children.remove(ch); // Cleanup unused branch
                return;
            }
            node = next;
        }
        node.wordCount--;
        if (node.wordCount == 0) {
            node.isEndOfWord = false;
        }
    }

    /**
     * Returns how many times a word was inserted.
     *
     * @param word the word to check
     * @return frequency of the word
     */
    public int countWordsEqualTo(String word) {
        TrieNode node = getNode(word);
        return (node != null && node.isEndOfWord) ? node.wordCount : 0;
    }

    /**
     * Returns how many words in the Trie start with the given prefix.
     *
     * @param prefix the prefix to match
     * @return count of words starting with the prefix
     */
    public int countWordsStartingWith(String prefix) {
        TrieNode node = getNode(prefix);
        return node != null ? node.prefixCount : 0;
    }

    /**
     * Returns all words stored in the Trie (duplicates included).
     *
     * @return list of all stored words
     */
    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        collectWords(root, new StringBuilder(), result);
        return result;
    }

    /**
     * Returns all words in the Trie that start with the given prefix.
     *
     * @param prefix the prefix to match
     * @return list of matching words
     */
    public List<String> getAllWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode node = getNode(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix), result);
        }
        return result;
    }

    /**
     * Finds the shortest prefix of a given word that exists as a full word in the Trie.
     *
     * @param word the word to check
     * @return the shortest matching root or null if none found
     */
    public String findShortestRoot(String word) {
        TrieNode current = root;
        StringBuilder prefix = new StringBuilder();
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) return null;
            current = current.children.get(ch);
            prefix.append(ch);
            if (current.isEndOfWord) return prefix.toString();
        }
        return null;
    }

    /**
     * Returns the TrieNode corresponding to the given string.
     *
     * @param s the string path to search
     * @return the corresponding node or null if not found
     */
    private TrieNode getNode(String s) {
        if (s == null) return null;

        TrieNode node = root;
        for (char ch : s.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) return null;
        }
        return node;
    }

    /**
     * DFS helper to collect words from the current node.
     *
     * @param node current TrieNode
     * @param sb current word prefix
     * @param result list to store results
     */
    private void collectWords(TrieNode node, StringBuilder sb, List<String> result) {
        if (node.isEndOfWord) {
            for (int i = 0; i < node.wordCount; i++) {
                result.add(sb.toString());
            }
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            sb.append(entry.getKey());
            collectWords(entry.getValue(), sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
