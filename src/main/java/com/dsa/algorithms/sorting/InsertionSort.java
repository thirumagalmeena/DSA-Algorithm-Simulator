package com.dsa.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;

public class InsertionSort implements Sortable {

    @Override
    public int[] sort(int[] arr) {
        int n = arr.length;

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }

        return arr;
    }

    @Override
    public List<int[]> sortWithSteps(int[] arr) {
        List<int[]> steps = new ArrayList<>();
        steps.add(arr.clone()); // initial state

        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;

            steps.add(arr.clone()); // capture after each insertion
        }

        return steps;
    }
}
