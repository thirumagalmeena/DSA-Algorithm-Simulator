package com.dsa.algorithms.sorting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

public class InsertionSortTest {

    private final Sortable algo = new InsertionSort();

    @Test
    void testNormalSort() {
        int[] arr = {5, 2, 9, 1, 5, 6};
        int[] expected = {1, 2, 5, 5, 6, 9};

        algo.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortWithSteps() {
        int[] arr = {4, 3, 2};
        List<int[]> steps = algo.sortWithSteps(Arrays.copyOf(arr, arr.length));

        int[] expected = {2, 3, 4};
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
        int[] arr = {99};
        algo.sort(arr);
        assertArrayEquals(new int[]{99}, arr);
    }

    @Test
    void testAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        algo.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }
}
