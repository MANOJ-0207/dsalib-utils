package io.github.manoj0207.dsalibutils.stringutils.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm based pattern matcher.
 * Efficiently finds all occurrences of a pattern in the text using
 * a linear-time Z-array technique on the concatenated string "pattern$text".
 */
public class ZAlgorithmMatcher extends AbstractPatternMatcher {

    /**
     * Computes the Z-array for a given character array.
     * Each Z[i] stores the length of the longest substring starting at i
     * that matches the prefix of the array.
     *
     * @param s the input character array (non-null)
     * @return the Z-array
     */
    private int[] computeZArray(char[] s) {
        int n = s.length;
        int[] z = new int[n];
        int l = 0, r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            while (i + z[i] < n && s[z[i]] == s[i + z[i]]) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }

    /**
     * Searches for all occurrences of the pattern in the given text using the Z-algorithm.
     *
     * @param text    the text to search in (non-null)
     * @param pattern the pattern to search for (non-null)
     * @return list of starting indices where the pattern occurs
     */
    @Override
    protected List<Integer> doSearch(char[] text, char[] pattern) {
        List<Integer> result = new ArrayList<>();

        int n = text.length;
        int m = pattern.length;
        if (m == 0 || m > n) return result;

        // Build concatenated array: pattern + '$' + text
        char[] concat = new char[m + 1 + n];
        System.arraycopy(pattern, 0, concat, 0, m);
        concat[m] = '$';
        System.arraycopy(text, 0, concat, m + 1, n);

        int[] z = computeZArray(concat);

        // Look for matches in the Z-array
        for (int i = m + 1; i < z.length; i++) {
            if (z[i] == m) {
                result.add(i - m - 1);  // Match found at this index in the original text
            }
        }

        return result;
    }
}
