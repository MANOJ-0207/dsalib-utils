package io.github.manoj0207.dsalibutils.stringutils;

import java.util.HashMap;
import java.util.Map;

/**
 * Node structure used in the {@link Trie} class.
 * Each node represents a character and tracks:
 * - its children
 * - end-of-word status
 * - number of exact word insertions
 * - number of prefixes passing through
 */
class TrieNode {
    final Map<Character, TrieNode> children;
    boolean isEndOfWord;
    int wordCount;
    int prefixCount;

    /**
     * Constructs an empty TrieNode.
     */
    TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.wordCount = 0;
        this.prefixCount = 0;
    }
}
