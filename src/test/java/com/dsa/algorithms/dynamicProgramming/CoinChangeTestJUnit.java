package com.dsa.algorithms.dynamicProgramming;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CoinChangeTestJUnit {

    @Test
    void testMinCoinsBasic() {
        CoinChange cc = new CoinChange();
        int[] coins = {1, 2, 5};
        int amount = 11;

        int minCoins = cc.minCoins(coins, amount);
        assertEquals(3, minCoins, "Minimum coins to make 11 should be 3 (5+5+1).");

        List<int[]> steps = cc.getMinCoinsSteps();
        assertNotNull(steps, "MinCoins steps should not be null.");
        assertTrue(steps.size() > 0, "Steps should be recorded for visualization.");
    }

    @Test
    void testMinCoinsImpossible() {
        CoinChange cc = new CoinChange();
        int[] coins = {2, 4};
        int amount = 7;

        int minCoins = cc.minCoins(coins, amount);
        assertEquals(-1, minCoins, "Impossible to make amount 7 with coins [2,4].");
    }

    @Test
    void testCountWaysBasic() {
        CoinChange cc = new CoinChange();
        int[] coins = {1, 2, 5};
        int amount = 5;

        int ways = cc.countWays(coins, amount);
        assertEquals(4, ways, "Ways to make 5 with coins [1,2,5] should be 4.");

        List<int[]> steps = cc.getWaysSteps();
        assertNotNull(steps, "Ways steps should not be null.");
        assertTrue(steps.size() > 0, "Steps should be recorded for visualization.");
    }

    @Test
    void testCountWaysNoCoins() {
        CoinChange cc = new CoinChange();
        int[] coins = {};
        int amount = 5;

        int ways = cc.countWays(coins, amount);
        assertEquals(0, ways, "No coins available, ways should be 0.");
    }

    @Test
    void testCountWaysZeroAmount() {
        CoinChange cc = new CoinChange();
        int[] coins = {1, 2, 3};
        int amount = 0;

        int ways = cc.countWays(coins, amount);
        assertEquals(1, ways, "Zero amount has exactly 1 way (use no coins).");
    }

    @Test
    void testMinCoinsZeroAmount() {
        CoinChange cc = new CoinChange();
        int[] coins = {1, 2, 3};
        int amount = 0;

        int minCoins = cc.minCoins(coins, amount);
        assertEquals(0, minCoins, "Zero amount requires 0 coins.");
    }

    @Test
    void testMinCoinsSingleCoin() {
        CoinChange cc = new CoinChange();
        int[] coins = {1};
        int amount = 10;

        int minCoins = cc.minCoins(coins, amount);
        assertEquals(10, minCoins, "10 amount with only 1-coin should require 10 coins.");
    }

    @Test
    void testCountWaysSingleCoin() {
        CoinChange cc = new CoinChange();
        int[] coins = {2};
        int amount = 6;

        int ways = cc.countWays(coins, amount);
        assertEquals(1, ways, "Only one way to make 6 with 2-coin repeatedly.");
    }
}
