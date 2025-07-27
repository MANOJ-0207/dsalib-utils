package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.List;

/**
 * Abstract base class for pattern matching algorithms.
 * Provides shared validation and input normalization logic.
 *
 * Subclasses should implement the core matching logic on character arrays
 * to maximize reuse across string and char[] inputs.
 */
public abstract class AbstractPatternMatcher implements PatternMatcher {

    /**
     * Validates inputs and converts Strings to char arrays before delegating.
     *
     * @param text    input text (non-null)
     * @param pattern input pattern (non-null)
     * @return list of starting indices where pattern is found
     * @throws IllegalArgumentException if text or pattern is null
     */
    @Override
    public List<Integer> search(String text, String pattern) {
        if (text == null || pattern == null) {
            throw new IllegalArgumentException("Text and pattern must not be null.");
        }
        return search(text.toCharArray(), pattern.toCharArray());
    }

    /**
     * Validates inputs and delegates to the core abstract matcher logic.
     *
     * @param text    input text array (non-null)
     * @param pattern pattern array (non-null)
     * @return list of starting indices where pattern is found
     * @throws IllegalArgumentException if text or pattern is null
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
     * @param text    text array
     * @param pattern pattern array
     * @return list of starting indices where pattern matches text
     */
    protected abstract List<Integer> doSearch(char[] text, char[] pattern);
}
