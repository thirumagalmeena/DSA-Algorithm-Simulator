package com.dsa.algorithms.greedy;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DijkstraAlgorithmTestJUnit {

    @Test
    void testDijkstraSmallGraph() {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
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

        int[] distance = dijkstra.findShortestPaths(graph, 0);

        int[] expected = {0, 4, 12, 19, 21, 11, 9, 8, 14};
        assertArrayEquals(expected, distance, "Dijkstra shortest path distances mismatch");
    }

    @Test
    void testDijkstraDisconnectedGraph() {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
        int[][] graph = {
                {0, 5, 0, 0},
                {5, 0, 0, 0},
                {0, 0, 0, 2},
                {0, 0, 2, 0}
        };

        int[] distance = dijkstra.findShortestPaths(graph, 0);
        assertEquals(0, distance[0]);
        assertEquals(5, distance[1]);
        assertEquals(Integer.MAX_VALUE, distance[2]);
        assertEquals(Integer.MAX_VALUE, distance[3]);
    }

    @Test
    void testStepRecording() {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
        int[][] graph = {
                {0, 10, 0, 30, 100},
                {10, 0, 50, 0, 0},
                {0, 50, 0, 20, 10},
                {30, 0, 20, 0, 60},
                {100, 0, 10, 60, 0}
        };

        dijkstra.findShortestPaths(graph, 0);
        List<DijkstraAlgorithm.Step> steps = dijkstra.getSteps();

        assertNotNull(steps, "Steps list should not be null");
        assertTrue(steps.size() > 0, "Steps should be recorded during computation");
        assertTrue(steps.get(0).distance.length == graph.length, "Each step should snapshot correct vertex count");
    }
}
