package com.dsa.algorithms.sorting;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortTest {

    private final Sortable algo = new MergeSort();

    @Test
    void testNormalSort() {
        int[] arr = {38, 27, 43, 3, 9, 82, 10};
        int[] expected = {3, 9, 10, 27, 38, 43, 82};

        algo.sort(arr);  // use interface instance
        assertArrayEquals(expected, arr, "Array should be sorted correctly");
    }

    @Test
    void testSortWithSteps() {
        int[] arr = {5, 2, 4, 7, 1};
        List<int[]> steps = algo.sortWithSteps(Arrays.copyOf(arr, arr.length));

        int[] expected = {1, 2, 4, 5, 7};
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
