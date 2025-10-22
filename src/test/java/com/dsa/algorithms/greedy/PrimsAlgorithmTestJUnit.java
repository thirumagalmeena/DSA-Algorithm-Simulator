package com.dsa.algorithms.greedy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * JUnit test cases for PrimsAlgorithm
 * 
 * Validates MST formation, cost correctness, handling of disconnected graphs,
 * and step recording for simulator visualization.
 */
public class PrimsAlgorithmTestJUnit {

    @Test
    public void testPrimsAlgorithm_MST_Creation() {
        PrimsAlgorithm prims = new PrimsAlgorithm();

        int[][] graph = {
            {0, 2, 0, 6, 0},
            {2, 0, 3, 8, 5},
            {0, 3, 0, 0, 7},
            {6, 8, 0, 0, 9},
            {0, 5, 7, 9, 0}
        };

        int[] parent = prims.findMST(graph);

        // Expected MST edges:
        // (0,1), (1,2), (1,4), (0,3)
        int expectedCost = 16;
        int actualCost = prims.getMSTCost(graph, parent);

        assertEquals(expectedCost, actualCost, "MST total cost mismatch");
        assertEquals(-1, parent[0], "Root node should have parent = -1");
    }

    @Test
    public void testPrimsAlgorithm_DisconnectedGraph() {
        PrimsAlgorithm prims = new PrimsAlgorithm();

        int[][] graph = {
            {0, 2, 0},
            {2, 0, 0},
            {0, 0, 0}
        };

        int[] parent = prims.findMST(graph);
        int cost = prims.getMSTCost(graph, parent);

        // Vertex 2 should remain unconnected
        assertEquals(Integer.MAX_VALUE, prims.getSteps().get(prims.getSteps().size() - 1).currentKey[2],
                     "Disconnected vertex key should remain infinity");
        assertTrue(cost < Integer.MAX_VALUE, "MST cost should be finite for connected components");
    }

    @Test
    public void testPrimsAlgorithm_SingleVertex() {
        PrimsAlgorithm prims = new PrimsAlgorithm();

        int[][] graph = {
            {0}
        };

        int[] parent = prims.findMST(graph);
        assertEquals(-1, parent[0], "Single vertex should have no parent");
        assertEquals(0, prims.getMSTCost(graph, parent), "MST cost for single vertex must be 0");
    }

    @Test
    public void testPrimsAlgorithm_StepRecording() {
        PrimsAlgorithm prims = new PrimsAlgorithm();

        int[][] graph = {
            {0, 4, 0, 0, 0, 0, 0, 8, 0},
            {4, 0, 8, 0, 0, 0, 0, 11, 0},
            {0, 8, 0, 7, 0, 4, 0, 0, 2},
            {0, 0, 7, 0, 9, 14, 0, 0, 0},
            {0, 0, 0, 9, 0, 10, 0, 0, 0},
            {0, 0, 4, 14, 10, 0, 2, 0, 0},
            {0, 0, 0, 0, 0, 2, 0, 1, 6},
            {8, 11, 0, 0, 0, 0, 1, 0, 7},
            {0, 0, 2, 0, 0, 0, 6, 7, 0}
        };

        prims.findMST(graph);
        List<PrimsAlgorithm.Step> steps = prims.getSteps();

        assertNotNull(steps, "Steps list should not be null");
        assertTrue(steps.size() > 0, "Steps should be recorded for visualization");

        PrimsAlgorithm.Step firstStep = steps.get(0);
        assertTrue(firstStep.currentKey.length == graph.length, "Each step must snapshot the full key array");
    }
}
