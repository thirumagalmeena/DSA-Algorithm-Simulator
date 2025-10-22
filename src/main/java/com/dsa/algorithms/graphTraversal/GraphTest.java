package com.dsa.algorithms.graphTraversal;

import java.util.*;

public class GraphTest {
    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(2, 3));
        graph.put(2, Arrays.asList(4, 5));
        graph.put(3, Arrays.asList(6));
        graph.put(4, Collections.emptyList());
        graph.put(5, Collections.emptyList());
        graph.put(6, Collections.emptyList());

        GraphTraversable bfs = new BFS(graph);
        GraphTraversable dfs = new DFS(graph);

        System.out.println("BFS Traversal: " + bfs.traverse(1));
        System.out.println("BFS Steps: " + bfs.getTraversalSteps());

        System.out.println("\nDFS Traversal: " + dfs.traverse(1));
        System.out.println("DFS Steps: " + dfs.getTraversalSteps());
    }
}
