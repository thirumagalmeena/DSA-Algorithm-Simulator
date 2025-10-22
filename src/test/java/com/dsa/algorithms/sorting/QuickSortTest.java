package com.dsa.algorithms.sorting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

public class QuickSortTest {

    private final Sortable algo = new QuickSort();

    @Test
    void testNormalSort() {
        int[] arr = {10, 7, 8, 9, 1, 5};
        int[] expected = {1, 5, 7, 8, 9, 10};

        algo.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testWithDuplicates() {
        int[] arr = {4, 2, 2, 8, 3, 3, 1};
        int[] expected = {1, 2, 2, 3, 3, 4, 8};

        algo.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testWithNegativeNumbers() {
        int[] arr = {0, -5, 2, -3, 1};
        int[] expected = {-5, -3, 0, 1, 2};

        algo.sort(arr);
        assertArrayEquals(expected, arr);
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
    void testSortWithSteps() {
        int[] arr = {3, 6, 2};
        List<int[]> steps = algo.sortWithSteps(Arrays.copyOf(arr, arr.length));

        int[] expected = {2, 3, 6};
        assertArrayEquals(expected, steps.get(steps.size() - 1));
    }
}
