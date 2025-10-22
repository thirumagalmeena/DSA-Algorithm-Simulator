package com.dsa.algorithms.dynamicProgramming;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Longest Common Subsequence (LCS) algorithm.
 * Tests correctness, boundary conditions, and DP step tracking.
 */
public class LongestCommonSubsequenceTestJUnit {

    @Test
    void testBasicLCS() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "abcde";
        String text2 = "ace";
        int result = lcs.lcs(text1, text2);

        assertEquals(3, result, "Expected LCS length is 3 ('ace').");
    }

    @Test
    void testNoCommonSubsequence() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "abc";
        String text2 = "def";
        int result = lcs.lcs(text1, text2);

        assertEquals(0, result, "No common subsequence expected.");
    }

    @Test
    void testIdenticalStrings() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "datascience";
        String text2 = "datascience";
        int result = lcs.lcs(text1, text2);

        assertEquals(text1.length(), result, "LCS of identical strings should be the full length.");
    }

    @Test
    void testSingleCharacterMatch() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "x";
        String text2 = "xyz";
        int result = lcs.lcs(text1, text2);

        assertEquals(1, result, "Single character match should return LCS length = 1.");
    }

    @Test
    void testEmptyStrings() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "";
        String text2 = "";
        int result = lcs.lcs(text1, text2);

        assertEquals(0, result, "Empty strings should have LCS length 0.");
    }

    @Test
    void testStepsRecorded() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "abc";
        String text2 = "ac";

        lcs.lcs(text1, text2);
        List<int[][]> steps = lcs.getSteps();

        assertNotNull(steps, "Steps list should not be null.");
        assertTrue(steps.size() > 0, "Steps should be recorded for visualization.");
        assertEquals(text1.length(), steps.size(), "Number of steps should equal length of text1.");
    }

    @Test
    void testComplexCase() {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        String text1 = "AGGTAB";
        String text2 = "GXTXAYB";
        int result = lcs.lcs(text1, text2);

        assertEquals(4, result, "Expected LCS length = 4 ('GTAB').");
    }
}
