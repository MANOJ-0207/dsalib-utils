package io.github.manoj0207.dsalibutils.stringutils.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KMPMatcherTest {

    private final PatternMatcher matcher = new KMPMatcher();

    @Test
    void testSingleMatch() {
        List<Integer> result = matcher.search("abcde", "bcd");
        assertEquals(List.of(1), result);
    }

    @Test
    void testMultipleMatches() {
        List<Integer> result = matcher.search("ababcabcab", "abc");
        assertEquals(List.of(2, 5), result);
    }

    @Test
    void testNoMatch() {
        List<Integer> result = matcher.search("abcdefgh", "xyz");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFullMatch() {
        List<Integer> result = matcher.search("pattern", "pattern");
        assertEquals(List.of(0), result);
    }

    @Test
    void testEmptyPattern() {
        List<Integer> result = matcher.search("anything", "");
        assertTrue(result.isEmpty());
    }

    @Test
    void testPatternLongerThanText() {
        List<Integer> result = matcher.search("short", "longerpattern");
        assertTrue(result.isEmpty());
    }

    @Test
    void testOverlappingMatches() {
        List<Integer> result = matcher.search("aaaaa", "aaa");
        assertEquals(List.of(0, 1, 2), result);  // overlapping matches
    }

    @Test
    void testPatternAtEnd() {
        List<Integer> result = matcher.search("helloworld", "world");
        assertEquals(List.of(5), result);
    }

    @Test
    void testPatternAtStart() {
        List<Integer> result = matcher.search("helloworld", "hello");
        assertEquals(List.of(0), result);
    }

    @Test
    void testPatternRepeatsFully() {
        List<Integer> result = matcher.search("abababab", "abab");
        assertEquals(List.of(0, 2, 4), result);
    }
}
