package com.dsa.algorithms.searching;

public class SearchTest {
    public static void main(String[] args) {
        int[] data = {5, 2, 8, 7, 1, 9, 3};

        Searchable linear = new LinearSearch();
        int result1 = linear.search(data, 9);
        System.out.println("Linear Search result: " + result1);
        System.out.println("Steps: " + java.util.Arrays.toString(linear.getSearchSteps()));

        java.util.Arrays.sort(data);
        Searchable binary = new BinarySearch();
        int result2 = binary.search(data, 9);
        System.out.println("Binary Search result: " + result2);
        System.out.println("Steps: " + java.util.Arrays.toString(binary.getSearchSteps()));
    }
}
