package com.dsa.algorithms.sorting;

import java.util.ArrayList;
import java.util.List;

public class MergeSort implements Sortable {

    @Override
    public int[] sort(int[] arr) {
        if (arr == null || arr.length < 2) return arr;
        mergeSort(arr, 0, arr.length - 1);
        return arr;
    }

    @Override
    public List<int[]> sortWithSteps(int[] arr) {
        List<int[]> steps = new ArrayList<>();
        if (arr == null || arr.length < 2) return steps;
        mergeSortWithSteps(arr, 0, arr.length - 1, steps);
        return steps;
    }

    private void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void mergeSortWithSteps(int[] arr, int left, int right, List<int[]> steps) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSortWithSteps(arr, left, mid, steps);
        mergeSortWithSteps(arr, mid + 1, right, steps);
        merge(arr, left, mid, right);

        steps.add(arr.clone()); // capture state after each merge
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
}
