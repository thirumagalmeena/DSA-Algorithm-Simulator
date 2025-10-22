package com.dsa.algorithms.graphTraversal;

import java.util.*;

public class BFS implements GraphTraversable {
    private Map<Integer, List<Integer>> graph;
    private List<String> steps = new ArrayList<>();

    public BFS(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    @Override
    public List<Integer> traverse(int startNode) {
        List<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(startNode);
        visited.add(startNode);
        steps.add("Start from node " + startNode);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);
            steps.add("Visited: " + current);

            for (int neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    steps.add("Queue: " + neighbor);
                }
            }
        }

        return result;
    }

    @Override
    public List<String> getTraversalSteps() {
        return steps;
    }
}
