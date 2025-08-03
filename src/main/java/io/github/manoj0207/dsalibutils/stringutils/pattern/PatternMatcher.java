package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.List;

/**
 * Interface for string pattern matching algorithms.
 * Provides overloaded methods to support both {@code String} and {@code char[]} inputs.
 * <p>
 * Implementations should return all starting indices where the pattern occurs in the text.
 */
public interface PatternMatcher {

    /**
     * Searches for all occurrences of the pattern in the given text.
     *
     * @param text    the main text to search within (must not be null)
     * @param pattern the pattern to search for (must not be null)
     * @return a list of starting indices where the pattern is found in the text
     * @throws IllegalArgumentException if {@code text} or {@code pattern} is null
     */
    List<Integer> search(String text, String pattern);

    /**
     * Searches for all occurrences of the pattern in the given character array.
     *
     * @param text    the main character array to search within (must not be null)
     * @param pattern the character array pattern to search for (must not be null)
     * @return a list of starting indices where the pattern is found in the text
     * @throws IllegalArgumentException if {@code text} or {@code pattern} is null
     */
    List<Integer> search(char[] text, char[] pattern);
}
