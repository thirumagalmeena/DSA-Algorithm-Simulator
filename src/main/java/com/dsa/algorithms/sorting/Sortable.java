package com.dsa.algorithms.sorting;

import java.util.List;

public interface Sortable {
    // Returns the final sorted array
    int[] sort(int[] arr);

    // Returns step-by-step states for visualization
    List<int[]> sortWithSteps(int[] arr);
}
