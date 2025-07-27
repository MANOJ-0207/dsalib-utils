package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.List;

/**
 * Interface for string pattern matching algorithms.
 * Provides overloaded methods to support both String and char[] inputs.
 *
 * Implementations should return all starting indices where the pattern is found in the text.
 */
public interface PatternMatcher {

    /**
     * Searches for all occurrences of the pattern in the given text.
     *
     * @param text    the main text to search within (must not be null)
     * @param pattern the pattern to search for (must not be null)
     * @return list of starting indices where pattern is found in text
     * @throws IllegalArgumentException if text or pattern is null
     */
    List<Integer> search(String text, String pattern);

    /**
     * Searches for all occurrences of the pattern in the given character array.
     *
     * @param text    the main char array to search within (must not be null)
     * @param pattern the pattern char array to search for (must not be null)
     * @return list of starting indices where pattern is found in text
     * @throws IllegalArgumentException if text or pattern is null
     */
    List<Integer> search(char[] text, char[] pattern);
}
