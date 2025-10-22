package com.dsa.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;

public class QuickSort implements Sortable {

    @Override
    public int[] sort(int[] arr) {
        if (arr == null || arr.length < 2) return arr;
        quicksort(arr, 0, arr.length - 1);
        return arr;
    }

    @Override
    public List<int[]> sortWithSteps(int[] arr) {
        List<int[]> steps = new ArrayList<>();
        if (arr == null || arr.length < 2) return steps;
        quicksortWithSteps(arr, 0, arr.length - 1, steps);
        return steps;
    }

    private void quicksort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quicksort(arr, low, pivotIndex - 1);
            quicksort(arr, pivotIndex + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void quicksortWithSteps(int[] arr, int low, int high, List<int[]> steps) {
        if (low < high) {
            int pivotIndex = partitionWithSteps(arr, low, high, steps);
            quicksortWithSteps(arr, low, pivotIndex - 1, steps);
            quicksortWithSteps(arr, pivotIndex + 1, high, steps);
        }
    }

    private int partitionWithSteps(int[] arr, int low, int high, List<int[]> steps) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
                steps.add(arr.clone());
            }
        }
        swap(arr, i + 1, high);
        steps.add(arr.clone());
        return i + 1;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
