package io.github.manoj0207.dsalibutils.stringutils;

import java.util.HashMap;
import java.util.Map;

/**
 * Node structure used in the {@link Trie} class.
 * <p>
 * Each node represents a character and tracks:
 * <ul>
 *     <li><b>children</b>: mapping of characters to child nodes</li>
 *     <li><b>isEndOfWord</b>: whether this node marks the end of a valid word</li>
 *     <li><b>wordCount</b>: number of times a word ends at this node</li>
 *     <li><b>prefixCount</b>: number of words passing through this node as a prefix</li>
 * </ul>
 */
class TrieNode {

    /** Map from character to child TrieNode */
    final Map<Character, TrieNode> children;

    /** Indicates if this node is the end of a complete word */
    boolean isEndOfWord;

    /** Number of exact word insertions ending at this node */
    int wordCount;

    /** Number of words that pass through this node as a prefix */
    int prefixCount;

    /**
     * Constructs an empty TrieNode with no children and zero counts.
     */
    TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.wordCount = 0;
        this.prefixCount = 0;
    }
}
