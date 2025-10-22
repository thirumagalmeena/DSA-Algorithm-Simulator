package com.dsa.algorithms.dynamicProgramming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Coin Change Problem
 * Given coins of different denominations and a total amount, compute:
 * 1. Minimum number of coins required to make the amount.
 * 2. Number of ways to make the amount.
 */
public class CoinChange {

    private final List<int[]> minCoinsSteps = new ArrayList<>();
    private final List<int[]> waysSteps = new ArrayList<>();

    public int minCoins(int[] coins, int amount) {
        minCoinsSteps.clear();
        int MAX = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, MAX);
        dp[0] = 0;

        minCoinsSteps.add(dp.clone()); // initial step

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (i - coin >= 0) {
                    dp[i] = Math.min(dp[i], 1 + dp[i - coin]);
                }
            }
            minCoinsSteps.add(dp.clone()); // record step
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    public int countWays(int[] coins, int amount) {
        waysSteps.clear();
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        waysSteps.add(dp.clone()); // initial step

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
            waysSteps.add(dp.clone()); // record step after each coin
        }

        return dp[amount];
    }

    public List<int[]> getMinCoinsSteps() {
        return minCoinsSteps;
    }


    public List<int[]> getWaysSteps() {
        return waysSteps;
    }
}
