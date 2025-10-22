package com.dsa.algorithms.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;

public class LongestCommonSubsequence {

    private final List<int[][]> steps = new ArrayList<>();

    public int lcs(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        steps.clear();

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
            // Record snapshot of the dp table after processing each character of text1
            steps.add(clone2DArray(dp));
        }

        return dp[m][n];
    }

    public List<int[][]> getSteps() {
        return steps;
    }

    private int[][] clone2DArray(int[][] src) {
        int[][] clone = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            clone[i] = src[i].clone();
        }
        return clone;
    }
}
