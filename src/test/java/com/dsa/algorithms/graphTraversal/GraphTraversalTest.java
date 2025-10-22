package com.dsa.algorithms.graphTraversal;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTraversalTest {

    private Map<Integer, List<Integer>> createSampleGraph() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(2, 3));
        graph.put(2, Arrays.asList(4, 5));
        graph.put(3, Arrays.asList(6));
        graph.put(4, Collections.emptyList());
        graph.put(5, Collections.emptyList());
        graph.put(6, Collections.emptyList());
        return graph;
    }

    @Test
    void testBFSTraversalOrder() {
        Map<Integer, List<Integer>> graph = createSampleGraph();
        GraphTraversable bfs = new BFS(graph);
        List<Integer> result = bfs.traverse(1);

        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5, 6);
        assertEquals(expected, result, "BFS should visit nodes level by level");
        assertFalse(bfs.getTraversalSteps().isEmpty(), "BFS steps should not be empty");
    }

    @Test
    void testDFSTraversalOrder() {
        Map<Integer, List<Integer>> graph = createSampleGraph();
        GraphTraversable dfs = new DFS(graph);
        List<Integer> result = dfs.traverse(1);

        List<Integer> expected = Arrays.asList(1, 2, 4, 5, 3, 6);
        assertEquals(expected, result, "DFS should visit nodes in depth-first order");
        assertFalse(dfs.getTraversalSteps().isEmpty(), "DFS steps should not be empty");
    }

    @Test
    void testEmptyGraphTraversal() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        GraphTraversable bfs = new BFS(graph);
        GraphTraversable dfs = new DFS(graph);

        assertEquals(Collections.singletonList(1), bfs.traverse(1), "BFS should just visit the start node");
        assertEquals(Collections.singletonList(1), dfs.traverse(1), "DFS should just visit the start node");
    }
}
