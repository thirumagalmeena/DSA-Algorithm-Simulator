package com.dsa.algorithms.greedy;

import java.util.*;

/**
 * Implementation of Topological Ordering using Kahn's Algorithm.
 * 
 * Captures each step of in-degree update and queue evolution
 * for visualization in the DSA Algorithm Simulator.
 */
public class TopologicalOrdering {

    /** Step representation for visualization */
    public static class Step {
        public List<Integer> order;         // current topological order
        public Queue<Integer> queueState;   // snapshot of current queue
        public int[] inDegree;              // snapshot of current in-degrees
        public String description;          // step explanation

        public Step(List<Integer> order, Queue<Integer> queue, int[] inDegree, String description) {
            this.order = new ArrayList<>(order);
            this.queueState = new LinkedList<>(queue);
            this.inDegree = inDegree.clone();
            this.description = description;
        }
    }

    private final List<Step> steps = new ArrayList<>();

    /**
     * Computes the topological order of a directed acyclic graph (DAG)
     * using Kahn's Algorithm.
     * 
     * @param graph adjacency list representation (0-indexed)
     * @return list representing one valid topological ordering
     */
    public List<Integer> findTopologicalOrder(List<List<Integer>> graph) {
        steps.clear();
        int n = graph.size();

        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                inDegree[v]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0)
                queue.offer(i);
        }

        List<Integer> order = new ArrayList<>();
        steps.add(new Step(order, queue, inDegree, "Initial queue with zero in-degree vertices"));

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            steps.add(new Step(order, queue, inDegree, "Removed vertex " + u + " from queue and added to order"));

            for (int v : graph.get(u)) {
                inDegree[v]--;
                steps.add(new Step(order, queue, inDegree,
                        "Decreased in-degree of vertex " + v + " to " + inDegree[v]));

                if (inDegree[v] == 0) {
                    queue.offer(v);
                    steps.add(new Step(order, queue, inDegree,
                            "Vertex " + v + " added to queue (in-degree became 0)"));
                }
            }
        }

        // If not all vertices are processed -> cycle exists
        if (order.size() != n) {
            steps.add(new Step(order, queue, inDegree,
                    "Cycle detected! Graph is not a DAG — topological ordering not possible."));
            return Collections.emptyList();
        }

        steps.add(new Step(order, queue, inDegree, "Topological ordering completed."));
        return order;
    }

    /** Returns recorded steps for visualization */
    public List<Step> getSteps() {
        return steps;
    }
}
