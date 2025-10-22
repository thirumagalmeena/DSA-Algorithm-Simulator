package com.dsa.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;

public class BubbleSort implements Sortable {

    @Override
    public int[] sort(int[] arr) {
        int n = arr.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return arr;
    }

    @Override
    public List<int[]> sortWithSteps(int[] arr) {
        List<int[]> steps = new ArrayList<>();
        steps.add(arr.clone()); // initial state

        int n = arr.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
                steps.add(arr.clone()); // capture after swap

            }
            if (!swapped) 
            {
                // Add final state if no swaps occurred
                steps.add(arr.clone());
                break;
            }
        }
        return steps;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
