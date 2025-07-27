package io.github.manoj0207.dsalibutils.stringutils.pattern;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RabinKarpMatcherTest {

    private final PatternMatcher matcher = new RabinKarpMatcher(31, 1_000_000_007); // safe large prime

    @Test
    void testBasicMatch() {
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
        List<Integer> result = matcher.search("abcdefg", "xyz");
        assertTrue(result.isEmpty());
    }

    @Test
    void testFullMatch() {
        List<Integer> result = matcher.search("pattern", "pattern");
        assertEquals(List.of(0), result);
    }

    @Test
    void testPatternAtEnd() {
        List<Integer> result = matcher.search("hello world", "world");
        assertEquals(List.of(6), result);
    }

    @Test
    void testPatternAtStart() {
        List<Integer> result = matcher.search("hello world", "hello");
        assertEquals(List.of(0), result);
    }

    @Test
    void testEmptyPattern() {
        List<Integer> result = matcher.search("nonempty", "");
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
        assertEquals(List.of(0, 1, 2), result); // overlapping
    }

    @Test
    void testSpecialCharacters() {
        List<Integer> result = matcher.search("a$b$c$", "$c$");
        assertEquals(List.of(3), result);
    }

    @Test
    void testSingleCharacterPattern() {
        List<Integer> result = matcher.search("mississippi", "s");
        assertEquals(List.of(2, 3, 5, 6), result);
    }

    @Test
    void testConstructorThrowsForInvalidArgs() {
        assertThrows(IllegalArgumentException.class, () -> new RabinKarpMatcher(0, 101));
        assertThrows(IllegalArgumentException.class, () -> new RabinKarpMatcher(31, -5));
    }
}
