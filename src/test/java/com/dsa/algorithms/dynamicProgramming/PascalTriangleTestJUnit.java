package com.dsa.algorithms.dynamicProgramming;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Pascal's Triangle generator.
 * Tests correctness of rows and step tracking for visualization.
 */
public class PascalTriangleTestJUnit {

    @Test
    void testFirstRow() {
        PascalTriangle pt = new PascalTriangle();
        List<Integer> row = pt.generate(1);

        assertEquals(1, row.size(), "First row should have 1 element.");
        assertEquals(1, (int) row.get(0), "First element should be 1.");

        List<List<Integer>> steps = pt.getSteps();
        assertEquals(1, steps.size(), "Steps should have 1 row.");
    }

    @Test
    void testFirstThreeRows() {
        PascalTriangle pt = new PascalTriangle();
        List<Integer> lastRow = pt.generate(3);

        assertEquals(List.of(1, 2, 1), lastRow, "3rd row should be [1, 2, 1].");

        List<List<Integer>> steps = pt.getSteps();
        assertEquals(3, steps.size(), "Steps should contain 3 rows.");
        assertEquals(List.of(1), steps.get(0));
        assertEquals(List.of(1, 1), steps.get(1));
        assertEquals(List.of(1, 2, 1), steps.get(2));
    }

    @Test
    void testZeroRows() {
        PascalTriangle pt = new PascalTriangle();
        List<Integer> row = pt.generate(0);

        assertTrue(row.isEmpty(), "Zero rows should return an empty list.");
        assertTrue(pt.getSteps().isEmpty(), "Steps should be empty for n=0.");
    }

    @Test
    void testFiveRows() {
        PascalTriangle pt = new PascalTriangle();
        List<Integer> lastRow = pt.generate(5);

        assertEquals(List.of(1, 4, 6, 4, 1), lastRow, "5th row should be [1, 4, 6, 4, 1].");

        List<List<Integer>> steps = pt.getSteps();
        assertEquals(5, steps.size(), "Steps should contain 5 rows.");
    }

    @Test
    void testStepsIntegrity() {
        PascalTriangle pt = new PascalTriangle();
        pt.generate(4);

        List<List<Integer>> steps = pt.getSteps();
        assertEquals(4, steps.size(), "Steps size should equal n.");
        assertEquals(List.of(1), steps.get(0));
        assertEquals(List.of(1, 1), steps.get(1));
        assertEquals(List.of(1, 2, 1), steps.get(2));
        assertEquals(List.of(1, 3, 3, 1), steps.get(3));
    }

    @Test
    void testLargeNumberOfRows() {
        PascalTriangle pt = new PascalTriangle();
        int n = 10;
        List<Integer> lastRow = pt.generate(n);

        assertEquals(n, lastRow.size(), "Last row size should equal n.");
        assertEquals(1, (int) lastRow.get(0), "First element should always be 1.");
        assertEquals(1, (int) lastRow.get(lastRow.size() - 1), "Last element should always be 1.");
    }
}
