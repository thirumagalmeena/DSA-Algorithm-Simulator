package com.dsa.algorithms.greedy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

/**
 * JUnit test cases for TopologicalOrdering
 * 
 * Validates correctness of topological sorting for DAGs,
 * detection of cycles, single-node handling, and step recording.
 */
public class TopologicalOrderingTestJUnit {

    @Test
    public void testTopologicalOrdering_ValidDAG() {
        TopologicalOrdering topo = new TopologicalOrdering();

        // Graph: 5 vertices
        // 0 -> 1, 0 -> 2, 1 -> 3, 2 -> 3, 3 -> 4
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph.add(new ArrayList<>());
        graph.get(0).addAll(Arrays.asList(1, 2));
        graph.get(1).add(3);
        graph.get(2).add(3);
        graph.get(3).add(4);

        List<Integer> order = topo.findTopologicalOrder(graph);

        // Possible valid orders: [0,1,2,3,4] or [0,2,1,3,4]
        assertTrue(order.equals(Arrays.asList(0, 1, 2, 3, 4)) ||
                   order.equals(Arrays.asList(0, 2, 1, 3, 4)),
                   "Topological order is not valid for given DAG");

        assertEquals(5, order.size(), "Topological order must include all vertices");
    }

    @Test
    public void testTopologicalOrdering_CycleDetection() {
        TopologicalOrdering topo = new TopologicalOrdering();

        // Graph with a cycle: 0 -> 1 -> 2 -> 0
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 3; i++) graph.add(new ArrayList<>());
        graph.get(0).add(1);
        graph.get(1).add(2);
        graph.get(2).add(0);

        List<Integer> order = topo.findTopologicalOrder(graph);

        assertTrue(order.isEmpty(), "Cycle detected — ordering should be empty");
        List<TopologicalOrdering.Step> steps = topo.getSteps();
        assertTrue(steps.get(steps.size() - 1).description.contains("Cycle detected"),
                   "Last step should indicate cycle detection");
    }

    @Test
    public void testTopologicalOrdering_SingleVertex() {
        TopologicalOrdering topo = new TopologicalOrdering();

        List<List<Integer>> graph = new ArrayList<>();
        graph.add(new ArrayList<>());

        List<Integer> order = topo.findTopologicalOrder(graph);

        assertEquals(Collections.singletonList(0), order, "Single vertex graph should return [0]");
    }

    @Test
    public void testTopologicalOrdering_StepRecording() {
        TopologicalOrdering topo = new TopologicalOrdering();

        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph.add(new ArrayList<>());
        graph.get(0).addAll(Arrays.asList(1, 2));
        graph.get(1).add(3);
        graph.get(2).add(3);

        topo.findTopologicalOrder(graph);
        List<TopologicalOrdering.Step> steps = topo.getSteps();

        assertNotNull(steps, "Steps list should not be null");
        assertTrue(steps.size() > 0, "Steps should be recorded for visualization");

        TopologicalOrdering.Step firstStep = steps.get(0);
        assertTrue(firstStep.inDegree.length == graph.size(),
                   "Each step must snapshot full in-degree array");
    }
}
