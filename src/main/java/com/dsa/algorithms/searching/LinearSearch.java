package com.dsa.algorithms.searching;

import java.util.ArrayList;
import java.util.List;

public class LinearSearch implements Searchable {
    private List<Integer> steps = new ArrayList<>();

    @Override
    public int search(int[] array, int target) {
        steps.clear();
        for (int i = 0; i < array.length; i++) {
            steps.add(i);  // track each checked index
            if (array[i] == target)
                return i;
        }
        return -1;
    }

    @Override
    public int[] getSearchSteps() {
        return steps.stream().mapToInt(i -> i).toArray();
    }
}
