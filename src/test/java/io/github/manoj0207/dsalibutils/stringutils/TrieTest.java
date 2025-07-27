package io.github.manoj0207.dsalibutils.stringutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    void testInsertAndSearch() {
        trie.insert("cat");
        trie.insert("car");

        assertTrue(trie.search("cat"));
        assertTrue(trie.search("car"));
        assertFalse(trie.search("can"));
    }

    @Test
    void testStartsWith() {
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.startsWith("ap"));
        assertTrue(trie.startsWith("app"));
        assertFalse(trie.startsWith("b"));
    }

    @Test
    void testCountWordsEqualTo() {
        trie.insert("dog");
        trie.insert("dog");
        trie.insert("dot");

        assertEquals(2, trie.countWordsEqualTo("dog"));
        assertEquals(1, trie.countWordsEqualTo("dot"));
        assertEquals(0, trie.countWordsEqualTo("do"));
    }

    @Test
    void testCountWordsStartingWith() {
        trie.insert("tree");
        trie.insert("trie");
        trie.insert("trip");
        trie.insert("trick");

        assertEquals(4, trie.countWordsStartingWith("tr"));
        assertEquals(3, trie.countWordsStartingWith("tri"));
        assertEquals(1, trie.countWordsStartingWith("trie"));
        assertEquals(0, trie.countWordsStartingWith("train"));
    }

    @Test
    void testDeleteSingleInstance() {
        trie.insert("sun");
        trie.insert("sun");
        trie.insert("sunny");

        trie.delete("sun");
        assertTrue(trie.search("sun"));
        assertEquals(1, trie.countWordsEqualTo("sun"));

        trie.delete("sun");
        assertFalse(trie.search("sun"));
        assertEquals(0, trie.countWordsEqualTo("sun"));

        // Delete non-existent word
        trie.delete("doesnotexist"); // Should not throw
    }

    @Test
    void testGetAllWords() {
        trie.insert("ant");
        trie.insert("bat");
        trie.insert("ant");

        List<String> words = trie.getAllWords();
        assertEquals(3, words.size());
        assertTrue(words.contains("ant"));
        assertTrue(words.contains("bat"));
    }

    @Test
    void testGetAllWordsWithPrefix() {
        trie.insert("hello");
        trie.insert("he");
        trie.insert("helium");
        trie.insert("hero");

        List<String> heWords = trie.getAllWordsWithPrefix("he");
        assertTrue(heWords.contains("hello"));
        assertTrue(heWords.contains("helium"));
        assertTrue(heWords.contains("hero"));
        assertTrue(heWords.contains("he"));

        List<String> helWords = trie.getAllWordsWithPrefix("hel");
        assertTrue(helWords.contains("hello"));
        assertTrue(helWords.contains("helium"));
        assertFalse(helWords.contains("hero"));
    }

    @Test
    void testFindShortestRoot() {
        trie.insert("rat");
        trie.insert("cat");
        trie.insert("bat");

        assertEquals("rat", trie.findShortestRoot("ratify"));
        assertEquals("bat", trie.findShortestRoot("battery"));
        assertNull(trie.findShortestRoot("dog"));
    }

    @Test
    void testInsertNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> trie.insert(null));
    }
}
