package com.dsa.algorithms.graphTraversal;

import java.util.*;

public class DFS implements GraphTraversable {
    private Map<Integer, List<Integer>> graph;
    private List<String> steps = new ArrayList<>();

    public DFS(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    @Override
    public List<Integer> traverse(int startNode) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfsHelper(startNode, visited, result);
        return result;
    }

    private void dfsHelper(int node, Set<Integer> visited, List<Integer> result) {
        visited.add(node);
        result.add(node);
        steps.add("Visited: " + node);

        for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                steps.add("Going deeper to: " + neighbor);
                dfsHelper(neighbor, visited, result);
            }
        }
    }

    @Override
    public List<String> getTraversalSteps() {
        return steps;
    }
}
