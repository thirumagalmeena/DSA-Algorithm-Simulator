package com.dsa.algorithms.dynamicProgramming;

import java.util.ArrayList;
import java.util.List;

public class Fibonacci {

    private List<Integer> steps; // store Fibonacci values step by step

    public Fibonacci() {
        steps = new ArrayList<>();
    }

    public int fib(int n) {
        steps.clear(); // clear previous steps

        if (n == 0) {
            steps.add(0);
            return 0;
        }
        if (n == 1) {
            steps.add(0);
            steps.add(1);
            return 1;
        }

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;

        steps.add(dp[0]);
        steps.add(dp[1]);

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
            steps.add(dp[i]); // store each step
        }

        return dp[n];
    }

    public List<Integer> getSteps() {
        return steps;
    }
}
