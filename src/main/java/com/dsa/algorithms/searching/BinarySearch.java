package com.dsa.algorithms.searching;

import java.util.ArrayList;
import java.util.List;

public class BinarySearch implements Searchable {
    private List<Integer> steps = new ArrayList<>();

    @Override
    public int search(int[] array, int target) {
        steps.clear();
        int low = 0, high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            steps.add(mid);

            if (array[mid] == target)
                return mid;
            else if (array[mid] < target)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }

    @Override
    public int[] getSearchSteps() {
        return steps.stream().mapToInt(i -> i).toArray();
    }
}
