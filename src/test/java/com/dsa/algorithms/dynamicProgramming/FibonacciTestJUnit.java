package com.dsa.algorithms.dynamicProgramming;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FibonacciTestJUnit {

    @Test
    void testFibSmallNumbers() {
        Fibonacci fib = new Fibonacci();

        assertEquals(0, fib.fib(0));
        assertEquals(1, fib.fib(1));
        assertEquals(1, fib.fib(2));
        assertEquals(2, fib.fib(3));
        assertEquals(3, fib.fib(4));
        assertEquals(5, fib.fib(5));
    }

    @Test
    void testFibSteps() {
        Fibonacci fib = new Fibonacci();
        fib.fib(5);

        List<Integer> steps = fib.getSteps();
        // Steps for n=5 should be [0, 1, 1, 2, 3, 5]
        assertArrayEquals(new int[]{0, 1, 1, 2, 3, 5}, steps.stream().mapToInt(Integer::intValue).toArray());
    }

    @Test
    void testFibLargerNumber() {
        Fibonacci fib = new Fibonacci();
        int result = fib.fib(10); // 10th Fibonacci
        assertEquals(55, result);

        List<Integer> steps = fib.getSteps();
        assertEquals(11, steps.size()); // Should have n+1 steps
        assertEquals(55, steps.get(10));
    }
}
