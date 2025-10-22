package com.dsa.algorithms.greedy;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class KruskalAlgorithmTestJUnit {

    @Test
    void testBasicMST() {
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        int[][] graph = {
                {0, 2, 0, 6, 0},
                {2, 0, 3, 8, 5},
                {0, 3, 0, 0, 7},
                {6, 8, 0, 0, 9},
                {0, 5, 7, 9, 0}
        };

        List<int[]> mst = kruskal.findMST(graph);
        int totalCost = kruskal.getMSTCost(mst);

        // Expected MST edges
        Set<String> expectedEdges = Set.of(
                "0-1", "1-2", "1-4", "0-3"
        );

        Set<String> actualEdges = new HashSet<>();
        for (int[] edge : mst) {
            actualEdges.add(edge[0] + "-" + edge[1]);
        }

        assertEquals(4, mst.size(), "MST should contain n-1 edges for 5 vertices.");
        assertEquals(16, totalCost, "Total MST cost should be 16.");
        assertEquals(expectedEdges, actualEdges, "MST edges mismatch.");
    }

    @Test
    void testStepRecording() {
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        int[][] graph = {
                {0, 1, 2},
                {1, 0, 4},
                {2, 4, 0}
        };

        kruskal.findMST(graph);
        List<KruskalAlgorithm.Step> steps = kruskal.getSteps();

        // Total edges = 3C2 = 3 edges
        assertEquals(3, steps.size(), "There should be 3 recorded steps.");

        // Check that at least one edge was taken
        long takenCount = steps.stream().filter(s -> s.taken).count();
        assertTrue(takenCount >= 2, "At least two edges should be taken to form MST.");

        // Ensure parent array evolves correctly
        KruskalAlgorithm.Step lastStep = steps.get(steps.size() - 1);
        int[] parent = lastStep.parent;
        assertTrue(parent[0] == parent[1] && parent[1] == parent[2],
                "All nodes should belong to the same set in final MST.");
    }

    @Test
    void testDisconnectedGraph() {
        KruskalAlgorithm kruskal = new KruskalAlgorithm();

        int[][] graph = {
                {0, 0, 0},
                {0, 0, 1},
                {0, 1, 0}
        };

        List<int[]> mst = kruskal.findMST(graph);
        int totalCost = kruskal.getMSTCost(mst);

        // Should only include one edge (1-2)
        assertEquals(1, mst.size(), "Only one edge should be included.");
        assertEquals(1, totalCost, "Total cost should be 1.");
    }
}
