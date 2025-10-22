package com.dsa.algorithms.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;

/**
 * 0/1 Knapsack Problem (Dynamic Programming)
 * 
 * Given weights and values of n items, put these items in a knapsack of capacity W 
 * to get the maximum total value in the knapsack. Each item can be taken or left.
 * 
 * DP Relation:
 *   dp[i][w] = max(dp[i-1][w], dp[i-1][w-weight[i-1]] + value[i-1]) if weight[i-1] <= w
 *   dp[i][w] = dp[i-1][w] otherwise
 */
public class Knapsack01 {

    private final List<int[]> steps = new ArrayList<>();

    public int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];
        steps.clear();

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= capacity; w++) {
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(
                            values[i - 1] + dp[i - 1][w - weights[i - 1]],
                            dp[i - 1][w]
                    );
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }

            int[] rowSnapshot = dp[i].clone();
            steps.add(rowSnapshot);
        }

        return dp[n][capacity];
    }


    public List<int[]> getSteps() {
        return steps;
    }
}
