package com.dsa.algorithms.dynamicProgramming;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for 0/1 Knapsack dynamic programming algorithm.
 * Tests correctness, edge cases, and step tracking.
 */
public class Knapsack01TestJUnit {

    @Test
    void testBasicKnapsack() {
        Knapsack01 knapsack = new Knapsack01();

        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        int result = knapsack.knapsack(weights, values, capacity);
        assertEquals(7, result, "Expected max value is 7 (items with weights 2 and 3).");
    }

    @Test
    void testKnapsackAllItemsFit() {
        Knapsack01 knapsack = new Knapsack01();

        int[] weights = {1, 2, 3};
        int[] values = {10, 15, 40};
        int capacity = 6;

        int result = knapsack.knapsack(weights, values, capacity);
        assertEquals(65, result, "All items fit; sum of all values should be 65.");
    }

    @Test
    void testKnapsackSingleItem() {
        Knapsack01 knapsack = new Knapsack01();

        int[] weights = {5};
        int[] values = {10};
        int capacity = 5;

        int result = knapsack.knapsack(weights, values, capacity);
        assertEquals(10, result, "Only one item fits exactly.");
    }

    @Test
    void testKnapsackNoFit() {
        Knapsack01 knapsack = new Knapsack01();

        int[] weights = {6, 7, 8};
        int[] values = {10, 20, 30};
        int capacity = 5;

        int result = knapsack.knapsack(weights, values, capacity);
        assertEquals(0, result, "No items fit, expected max value = 0.");
    }

    @Test
    void testKnapsackSteps() {
        Knapsack01 knapsack = new Knapsack01();

        int[] weights = {1, 3, 4, 5};
        int[] values = {1, 4, 5, 7};
        int capacity = 7;

        knapsack.knapsack(weights, values, capacity);
        List<int[]> steps = knapsack.getSteps();

        assertNotNull(steps, "Steps list should not be null.");
        assertTrue(steps.size() > 0, "Steps list should not be empty.");
        assertEquals(8, steps.get(steps.size() - 1).length, "Each step should represent DP row for capacity+1 values.");
    }

    @Test
    void testLargeInput() {
        Knapsack01 knapsack = new Knapsack01();

        int n = 10;
        int[] weights = {1,2,3,4,5,6,7,8,9,10};
        int[] values = {10,20,30,40,50,60,70,80,90,100};
        int capacity = 15;

        int result = knapsack.knapsack(weights, values, capacity);
        assertEquals(150, result, "Expected max value 150 (items 5 and 10 or similar combination).");
    }
}
