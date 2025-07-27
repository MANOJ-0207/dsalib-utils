package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) pattern matcher.
 * Efficiently finds all occurrences of a pattern in the text using
 * preprocessed longest prefix suffix (LPS) information.
 */
public class KMPMatcher extends AbstractPatternMatcher {

    /**
     * Performs the actual KMP pattern matching logic using character arrays.
     *
     * @param text    the input text (non-null)
     * @param pattern the input pattern (non-null)
     * @return list of all starting indices where the pattern matches in the text
     */
    @Override
    protected List<Integer> doSearch(char[] text, char[] pattern) {
        List<Integer> result = new ArrayList<>();

        int n = text.length;
        int m = pattern.length;

        if (m == 0 || n < m) {
            return result; // No matches possible
        }

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < n) {
            if (text[i] == pattern[j]) {
                i++;
                j++;
            }

            if (j == m) {
                result.add(i - j);     // Match found
                j = lps[j - 1];        // Prepare for next potential match
            } else if (i < n && text[i] != pattern[j]) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return result;
    }

    /**
     * Builds the Longest Prefix Suffix (LPS) array for the pattern.
     * The LPS array helps to skip unnecessary comparisons during the search phase.
     *
     * @param pattern the pattern as a char array (non-null)
     * @return the LPS array
     */
    private int[] buildLPS(char[] pattern) {
        int m = pattern.length;
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern[i] == pattern[len]) {
                lps[i++] = ++len;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i++] = 0;
                }
            }
        }

        return lps;
    }
}
