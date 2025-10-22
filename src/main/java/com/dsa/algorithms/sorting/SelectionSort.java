package com.dsa.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;

public class SelectionSort implements Sortable {

    @Override
    public int[] sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            int temp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }

    @Override
    public List<int[]> sortWithSteps(int[] arr) {
        List<int[]> steps = new ArrayList<>();
        steps.add(arr.clone()); // initial state

        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) minIdx = j;
            }

            // Swap
            int temp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = temp;

            steps.add(arr.clone()); // capture state after each swap
        }
        return steps;
    }
}

/*
Sortable algo = new SelectionSort();
int[] sorted = algo.sort(arr);              // final result
List<int[]> steps = algo.sortWithSteps(arr); // for visualization

 */