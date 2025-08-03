package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp string matcher using rolling hash.
 * <p>
 * Supports configurable hash base and modulus to manage collisions and overflow.
 */
public class RabinKarpMatcher extends AbstractPatternMatcher {

    private final int base;
    private final int mod;

    /**
     * Constructs a Rabin-Karp matcher with the given base and modulus.
     *
     * @param base the base used for hashing (e.g., 31 or 101)
     * @param mod  the modulus to avoid overflow (e.g., a large prime)
     * @throws IllegalArgumentException if base or mod are non-positive
     */
    public RabinKarpMatcher(int base, int mod) {
        if (base <= 0 || mod <= 0) {
            throw new IllegalArgumentException("Base and modulus must be positive integers.");
        }
        this.base = base;
        this.mod = mod;
    }

    /**
     * <b>Time Complexity:</b> <p>O(N + M)</p> average case, where N is the length of text and M is the length of pattern.
     * <p>
     * Performs Rabin-Karp pattern matching using rolling hash on character arrays.
     *
     * @param text    the text as character array (non-null)
     * @param pattern the pattern as character array (non-null)
     * @return list of starting indices where the pattern matches in the text
     */
    @Override
    protected List<Integer> doSearch(char[] text, char[] pattern) {
        List<Integer> result = new ArrayList<>();

        int n = text.length;
        int m = pattern.length;

        if (m == 0 || m > n) return result;

        long patternHash = 0;
        long textHash = 0;
        long power = 1;

        // Precompute pattern hash and initial text hash
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern[i]) % mod;
            textHash = (textHash * base + text[i]) % mod;
            if (i > 0) power = (power * base) % mod;
        }

        for (int i = 0; i <= n - m; i++) {
            // On hash match, verify actual characters to avoid false positives
            if (patternHash == textHash && isEqual(text, i, pattern)) {
                result.add(i);
            }

            // Roll the hash for next window
            if (i < n - m) {
                textHash = (textHash - (text[i] * power) % mod + mod) % mod;
                textHash = (textHash * base + text[i + m]) % mod;
            }
        }

        return result;
    }

    /**
     * <b>Time Complexity:</b> <p>O(M)</p> per call.
     * <p>
     * Utility method to compare characters between a text segment and pattern.
     *
     * @param text    the input text array
     * @param start   the start index in the text to compare
     * @param pattern the pattern to match
     * @return {@code true} if the substring matches the pattern, {@code false} otherwise
     */
    private boolean isEqual(char[] text, int start, char[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            if (text[start + i] != pattern[i]) return false;
        }
        return true;
    }
}
