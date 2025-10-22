package com.dsa.algorithms.searching;

public interface Searchable {
    // Searches for a target value in the given array.
    // array  - The array to search in.
    // target -  The value to search for.
    int search(int[] array, int target);

    // Returns an array of indices visited during the search,
    int[] getSearchSteps();
}
