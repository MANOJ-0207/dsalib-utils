package io.github.manoj0207.dsalibutils.stringutils.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZAlgorithmMatcherTest {

    private final PatternMatcher matcher = new ZAlgorithmMatcher();

    @Test
    void testSingleMatch() {
        List<Integer> result = matcher.search("abracadabra", "cada");
        assertEquals(List.of(4), result);
    }

    @Test
    void testMultipleMatches() {
        List<Integer> result = matcher.search("abcabcabc", "abc");
        assertEquals(List.of(0, 3, 6), result);
    }

    @Test
    void testNoMatch() {
        List<Integer> result = matcher.search("abcdef", "gh");
        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyPattern() {
        List<Integer> result = matcher.search("nonempty", "");
        assertTrue(result.isEmpty());
    }

    @Test
    void testPatternLongerThanText() {
        List<Integer> result = matcher.search("short", "toolongpattern");
        assertTrue(result.isEmpty());
    }

    @Test
    void testPatternAtStart() {
        List<Integer> result = matcher.search("patternatstart", "pattern");
        assertEquals(List.of(0), result);
    }

    @Test
    void testPatternAtEnd() {
        List<Integer> result = matcher.search("endswithpattern", "pattern");
        assertEquals(List.of(8), result);
    }

    @Test
    void testFullMatch() {
        List<Integer> result = matcher.search("exact", "exact");
        assertEquals(List.of(0), result);
    }

    @Test
    void testOverlappingMatches() {
        List<Integer> result = matcher.search("aaaaa", "aaa");
        assertEquals(List.of(0, 1, 2), result); // overlapping matches
    }

    @Test
    void testSpecialCharacters() {
        List<Integer> result = matcher.search("a$b$c$", "$c$");
        assertEquals(List.of(3), result);
    }

    @Test
    void testSingleCharacter() {
        List<Integer> result = matcher.search("banana", "a");
        assertEquals(List.of(1, 3, 5), result);
    }
}
