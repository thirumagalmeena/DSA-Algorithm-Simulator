package com.dsa.algorithms.sorting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

public class BubbleSortTest {

    private final Sortable algo = new BubbleSort(); // use via interface

    @Test
    void testNormalSort() {
        int[] arr = {5, 1, 4, 2, 8};
        int[] expected = {1, 2, 4, 5, 8};

        algo.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortWithSteps() {
        int[] arr = {5, 1, 4};
        List<int[]> steps = algo.sortWithSteps(Arrays.copyOf(arr, arr.length));

        // Step 1
        assertArrayEquals(new int[]{1, 5, 4}, steps.get(1));

        // Step 2
        assertArrayEquals(new int[]{1, 4, 5}, steps.get(2));

        // Final step
        int[] expected = {1, 4, 5};
        assertArrayEquals(expected, steps.get(steps.size() - 1));
    }

    @Test
    void testEmptyArray() {
        int[] arr = {};
        algo.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testSingleElement() {
        int[] arr = {42};
        algo.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        algo.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }
}
