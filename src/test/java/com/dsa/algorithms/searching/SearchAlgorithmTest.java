package com.dsa.algorithms.searching;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SearchAlgorithmTest {

    @Test
    void testLinearSearchFound() {
        Searchable linear = new LinearSearch();
        int[] data = {5, 2, 8, 7, 1, 9, 3};
        int index = linear.search(data, 9);

        assertEquals(5, index, "Linear Search should find 9 at index 5");
        assertTrue(linear.getSearchSteps().length > 0, "Steps should be recorded");
    }

    @Test
    void testLinearSearchNotFound() {
        Searchable linear = new LinearSearch();
        int[] data = {1, 2, 3, 4, 5};
        int index = linear.search(data, 9);

        assertEquals(-1, index, "Should return -1 when element not found");
    }

    @Test
    void testBinarySearchFound() {
        Searchable binary = new BinarySearch();
        int[] data = {1, 3, 5, 7, 9, 11, 13};
        int index = binary.search(data, 9);

        assertEquals(4, index, "Binary Search should find 9 at index 4");
        assertTrue(binary.getSearchSteps().length > 0, "Steps should be recorded");
    }

    @Test
    void testBinarySearchNotFound() {
        Searchable binary = new BinarySearch();
        int[] data = {2, 4, 6, 8, 10};
        int index = binary.search(data, 5);

        assertEquals(-1, index, "Binary Search should return -1 when not found");
    }

    @Test
    void testBinarySearchSingleElement() {
        Searchable binary = new BinarySearch();
        int[] data = {10};
        int index = binary.search(data, 10);

        assertEquals(0, index, "Binary Search should find element at index 0");
    }
}
