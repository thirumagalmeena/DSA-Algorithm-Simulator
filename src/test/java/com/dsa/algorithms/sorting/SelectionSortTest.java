package com.dsa.algorithms.sorting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;
import com.dsa.algorithms.sorting.Sortable;

public class SelectionSortTest {

    private final Sortable algo = new SelectionSort();

    @Test
    void testNormalSort() {
        int[] arr = {64, 25, 12, 22, 11};
        int[] expected = {11, 12, 22, 25, 64};

        algo.sort(arr);
        assertArrayEquals(expected, arr, "Array should be sorted in ascending order");
    }

    @Test
    void testSortWithSteps() {
        int[] arr = {64, 25, 12, 22, 11};
        List<int[]> steps = algo.sortWithSteps(Arrays.copyOf(arr, arr.length));

        // Verify we have the correct number of steps (initial + 4 swaps for 5 elements)
        assertEquals(5, steps.size(), "Should have initial state + 4 swap steps");

        // Step 0: Initial state (before any sorting)
        assertArrayEquals(new int[]{64, 25, 12, 22, 11}, steps.get(0));

        // Step 1: After first swap (11 with 64)
        assertArrayEquals(new int[]{11, 25, 12, 22, 64}, steps.get(1));

        // Step 2: After second swap (12 with 25)
        assertArrayEquals(new int[]{11, 12, 25, 22, 64}, steps.get(2));

        // Step 3: After third swap (22 with 25)
        assertArrayEquals(new int[]{11, 12, 22, 25, 64}, steps.get(3));

        // Step 4: Final state (no swap needed for last element)
        assertArrayEquals(new int[]{11, 12, 22, 25, 64}, steps.get(4));
    }

    @Test
    void testEmptyArray() {
        int[] arr = {};
        algo.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testSingleElement() {
        int[] arr = {5};
        algo.sort(arr);
        assertArrayEquals(new int[]{5}, arr);
    }

    @Test
    void testSortWithStepsEmptyArray() {
        int[] arr = {};
        List<int[]> steps = algo.sortWithSteps(arr);
        
        assertEquals(1, steps.size()); // Only initial state
        assertArrayEquals(new int[]{}, steps.get(0));
    }

    @Test
    void testSortWithStepsSingleElement() {
        int[] arr = {5};
        List<int[]> steps = algo.sortWithSteps(arr);
        
        assertEquals(1, steps.size()); // Only initial state (no swaps needed)
        assertArrayEquals(new int[]{5}, steps.get(0));
    }
}