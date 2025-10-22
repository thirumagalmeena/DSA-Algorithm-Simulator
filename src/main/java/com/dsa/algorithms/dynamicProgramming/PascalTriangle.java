package com.dsa.algorithms.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;


public class PascalTriangle {

    private final List<List<Integer>> steps = new ArrayList<>();

    public List<Integer> generate(int n) {
        steps.clear();

        if (n <= 0) return new ArrayList<>();

        List<Integer> prevRow = new ArrayList<>();
        prevRow.add(1);
        steps.add(new ArrayList<>(prevRow)); // Step 1

        for (int i = 1; i < n; i++) {
            List<Integer> row = new ArrayList<>();
            row.add(1); // first element

            for (int j = 1; j < prevRow.size(); j++) {
                row.add(prevRow.get(j - 1) + prevRow.get(j));
            }

            row.add(1); // last element
            steps.add(new ArrayList<>(row)); // record step
            prevRow = row;
        }

        return prevRow; // last row
    }

    public List<List<Integer>> getSteps() {
        return steps;
    }
}
