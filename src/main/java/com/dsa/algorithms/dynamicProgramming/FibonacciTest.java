package com.dsa.algorithms.dynamicProgramming;

import java.util.List;

public class FibonacciTest {
    public static void main(String[] args) {
        Fibonacci fib = new Fibonacci();

        int n = 10; // find 10th Fibonacci
        int result = fib.fib(n);

        System.out.println("Fibonacci of " + n + " is: " + result);

        List<Integer> steps = fib.getSteps();
        System.out.println("DP steps: " + steps);
    }
}
