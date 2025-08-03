package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.List;

/**
 * Abstract base class for pattern matching algorithms.
 * <p>
 * Provides shared validation and input normalization logic.
 * Subclasses should implement the core matching logic on character arrays
 * to maximize reuse across {@code String} and {@code char[]} inputs.
 */
public abstract class AbstractPatternMatcher implements PatternMatcher {

    /**
     * Validates input strings and converts them to character arrays before delegating to {@link #search(char[], char[])}.
     *
     * @param text    the input text (must not be {@code null})
     * @param pattern the pattern to search for (must not be {@code null})
     * @return list of starting indices where the pattern is found in the text
     * @throws IllegalArgumentException if {@code text} or {@code pattern} is {@code null}
     */
    @Override
    public List<Integer> search(String text, String pattern) {
        if (text == null || pattern == null) {
            throw new IllegalArgumentException("Text and pattern must not be null.");
        }
        return search(text.toCharArray(), pattern.toCharArray());
    }

    /**
     * Validates input character arrays and delegates to the core matcher implementation.
     *
     * @param text    the input character array (must not be {@code null})
     * @param pattern the pattern character array (must not be {@code null})
     * @return list of starting indices where the pattern is found in the text
     * @throws IllegalArgumentException if {@code text} or {@code pattern} is {@code null}
     */
    @Override
    public List<Integer> search(char[] text, char[] pattern) {
        if (text == null || pattern == null) {
            throw new IllegalArgumentException("Text and pattern arrays must not be null.");
        }
        return doSearch(text, pattern);
    }

    /**
     * Subclasses must implement this method to perform actual pattern matching logic.
     *
     * @param text    the input text array
     * @param pattern the pattern array to search for
     * @return list of starting indices where the pattern matches the text
     */
    protected abstract List<Integer> doSearch(char[] text, char[] pattern);
}
